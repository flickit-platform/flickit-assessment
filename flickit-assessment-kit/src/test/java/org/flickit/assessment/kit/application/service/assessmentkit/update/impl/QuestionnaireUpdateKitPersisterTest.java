package org.flickit.assessment.kit.application.service.assessmentkit.update.impl;

import lombok.SneakyThrows;
import org.flickit.assessment.kit.application.domain.AssessmentKit;
import org.flickit.assessment.kit.application.domain.Questionnaire;
import org.flickit.assessment.kit.application.domain.dsl.AssessmentKitDslModel;
import org.flickit.assessment.kit.application.port.out.questionnaire.CreateQuestionnairePort;
import org.flickit.assessment.kit.application.port.out.questionnaire.UpdateQuestionnairePort;
import org.flickit.assessment.kit.application.service.DslTranslator;
import org.flickit.assessment.kit.test.fixture.application.AssessmentKitMother;
import org.flickit.assessment.kit.test.fixture.application.Constants;
import org.flickit.assessment.kit.test.fixture.application.QuestionnaireMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.flickit.assessment.kit.test.fixture.application.Constants.*;
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
    @SneakyThrows
    void testQuestionnaireUpdateKitPersister_SameInputsAsDatabaseData_NoChange() {
        Long kitId = 1L;
        var savedQuestionnaire1 = QuestionnaireMother.questionnaire(QUESTIONNAIRE_CODE1, QUESTIONNAIRE_TITLE1, 1);
        var savedQuestionnaire2 = QuestionnaireMother.questionnaire(QUESTIONNAIRE_CODE2, QUESTIONNAIRE_TITLE2, 2);
        AssessmentKit savedKit = AssessmentKitMother.kitWithQuestionnaire(List.of(savedQuestionnaire1, savedQuestionnaire2), kitId);

        String dslContent = new String(Files.readAllBytes(Paths.get(FILE)));
        AssessmentKitDslModel dslKit = DslTranslator.parseJson(dslContent);
        persister.persist(savedKit, dslKit);

        verifyNoInteractions(createQuestionnairePort, updateQuestionnairePort);
    }

    @Test
    @SneakyThrows
    void testQuestionnaireUpdateKitPersister_QuestionnaireAdded_AddToDatabase() {
        Long kitId = 1L;
        var savedQuestionnaire1 = QuestionnaireMother.questionnaire(QUESTIONNAIRE_CODE1, QUESTIONNAIRE_TITLE1, 1);
        AssessmentKit savedKit = AssessmentKitMother.kitWithQuestionnaire(List.of(savedQuestionnaire1), kitId);

        when(createQuestionnairePort.persist(any(Questionnaire.class), eq(kitId))).thenReturn(1L);

        String dslContent = new String(Files.readAllBytes(Paths.get(FILE)));
        AssessmentKitDslModel dslKit = DslTranslator.parseJson(dslContent);
        persister.persist(savedKit, dslKit);

        verify(createQuestionnairePort, times(1)).persist(any(Questionnaire.class), eq(kitId));
        verifyNoInteractions(updateQuestionnairePort);
    }

    @Test
    @SneakyThrows
    void testQuestionnaireUpdateKitPersister_QuestionnaireUpdated_UpdateInDatabase() {
        Long kitId = 1L;
        var savedQuestionnaire1 = QuestionnaireMother.questionnaire(QUESTIONNAIRE_CODE1, QUESTIONNAIRE_TITLE1, 1);
        var savedQuestionnaire2 = QuestionnaireMother.questionnaire(QUESTIONNAIRE_CODE2, QUESTIONNAIRE_OLD_TITLE2, 2);
        AssessmentKit savedKit = AssessmentKitMother.kitWithQuestionnaire(List.of(savedQuestionnaire1, savedQuestionnaire2), kitId);

        doNothing().when(updateQuestionnairePort).update(any(UpdateQuestionnairePort.Param.class));

        String dslContent = new String(Files.readAllBytes(Paths.get(FILE)));
        AssessmentKitDslModel dslKit = DslTranslator.parseJson(dslContent);
        persister.persist(savedKit, dslKit);

        verify(updateQuestionnairePort, times(1)).update(any(UpdateQuestionnairePort.Param.class));
        verifyNoInteractions(createQuestionnairePort);
    }
}
