package org.flickit.assessment.kit.application.service.assessmentkit.update.impl;

import org.flickit.assessment.kit.application.domain.AssessmentKit;
import org.flickit.assessment.kit.application.domain.Questionnaire;
import org.flickit.assessment.kit.application.domain.dsl.AssessmentKitDslModel;
import org.flickit.assessment.kit.application.domain.dsl.QuestionnaireDslModel;
import org.flickit.assessment.kit.application.port.out.questionnaire.CreateQuestionnairePort;
import org.flickit.assessment.kit.application.port.out.questionnaire.UpdateQuestionnairePort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.flickit.assessment.kit.test.fixture.application.AssessmentKitMother.kitWithQuestionnaires;
import static org.flickit.assessment.kit.test.fixture.application.QuestionnaireMother.questionnaireWithTitle;
import static org.flickit.assessment.kit.test.fixture.application.dsl.QuestionnaireDslModelMother.domainToDslModel;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuestionnaireUpdateKitPersisterTest {

    @InjectMocks
    private QuestionnaireUpdateKitPersister persister;

    @Mock
    private CreateQuestionnairePort createQuestionnairePort;

    @Mock
    private UpdateQuestionnairePort updateQuestionnairePort;

    @Test
    void testPersist_TwoSameQuestionnairesInDbAnDsl_NoUpdate() {
        var savedQuestionnaire1 = questionnaireWithTitle("Clean Architecture");
        var savedQuestionnaire2 = questionnaireWithTitle("Code Quality");
        AssessmentKit savedKit = kitWithQuestionnaires(List.of(savedQuestionnaire1, savedQuestionnaire2));

        QuestionnaireDslModel dslQOne = domainToDslModel(savedQuestionnaire1);
        QuestionnaireDslModel dslQTwo = domainToDslModel(savedQuestionnaire2);
        AssessmentKitDslModel dslKit = AssessmentKitDslModel.builder()
            .questionnaires(List.of(dslQOne, dslQTwo))
            .build();

        persister.persist(savedKit, dslKit);

        verifyNoInteractions(createQuestionnairePort, updateQuestionnairePort);
    }

    @Test
    void testPersist_OneNewQuestionnaireInDsl_Create() {
        var savedQuestionnaire1 = questionnaireWithTitle("Clean Architecture");
        AssessmentKit savedKit = kitWithQuestionnaires(List.of(savedQuestionnaire1));

        QuestionnaireDslModel dslQOne = domainToDslModel(savedQuestionnaire1);
        Questionnaire newQuestionnaire = questionnaireWithTitle("Test");
        QuestionnaireDslModel dslQTwo = domainToDslModel(newQuestionnaire);

        AssessmentKitDslModel dslKit = AssessmentKitDslModel.builder()
            .questionnaires(List.of(dslQOne, dslQTwo))
            .build();

        when(createQuestionnairePort.persist(any(Questionnaire.class), eq(savedKit.getId()))).thenReturn(1L);

        persister.persist(savedKit, dslKit);

        verifyNoInteractions(updateQuestionnairePort);
    }

    @Test
    void testPersist_DslQuestionnaireTwoHasDifferentTitle_Update() {
        var savedQuestionnaire1 = questionnaireWithTitle("Clean Architecture");
        var savedQuestionnaire2 = questionnaireWithTitle("Old Code Quality");
        AssessmentKit savedKit = kitWithQuestionnaires(List.of(savedQuestionnaire1, savedQuestionnaire2));

        QuestionnaireDslModel dslQOne = domainToDslModel(savedQuestionnaire1);
        QuestionnaireDslModel dslQTwo = domainToDslModel(savedQuestionnaire2, b -> b.title("Test"));
        AssessmentKitDslModel dslKit = AssessmentKitDslModel.builder()
            .questionnaires(List.of(dslQOne, dslQTwo))
            .build();

        doNothing().when(updateQuestionnairePort).update(any(UpdateQuestionnairePort.Param.class));

        persister.persist(savedKit, dslKit);

        verifyNoInteractions(createQuestionnairePort);
    }
}
