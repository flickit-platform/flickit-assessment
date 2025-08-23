package org.flickit.assessment.kit.application.service.assessmentkit.createbydsl.impl;

import org.flickit.assessment.kit.application.domain.Questionnaire;
import org.flickit.assessment.kit.application.domain.dsl.AssessmentKitDslModel;
import org.flickit.assessment.kit.application.domain.dsl.QuestionnaireDslModel;
import org.flickit.assessment.kit.application.port.out.questionnaire.CreateQuestionnairePort;
import org.flickit.assessment.kit.application.service.assessmentkit.createbydsl.CreateKitPersisterContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.flickit.assessment.kit.application.service.assessmentkit.createbydsl.CreateKitPersisterContext.KEY_QUESTIONNAIRES;
import static org.flickit.assessment.kit.test.fixture.application.QuestionnaireMother.questionnaireWithTitle;
import static org.flickit.assessment.kit.test.fixture.application.dsl.QuestionnaireDslModelMother.domainToDslModel;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class QuestionnaireCreateKitPersisterTest {

    private static final Long KIT_ID = 1L;
    private static final UUID CURRENT_USER_ID = UUID.randomUUID();
    @InjectMocks
    private QuestionnaireCreateKitPersister persister;
    @Mock
    private CreateQuestionnairePort createQuestionnairePort;

    @Test
    void testOrder() {
        Assertions.assertEquals(3, persister.order());
    }

    @Test
    void testPersist_ValidInputs_SaveQuestionnaire() {
        var questionnaire1 = questionnaireWithTitle("Clean Architecture");
        var questionnaire2 = questionnaireWithTitle("Code Quality");

        QuestionnaireDslModel dslQOne = domainToDslModel(questionnaire1);
        QuestionnaireDslModel dslQTwo = domainToDslModel(questionnaire2);
        CreateKitPersisterContext context = new CreateKitPersisterContext();
        AssessmentKitDslModel dslModel = AssessmentKitDslModel.builder()
            .questionnaires(List.of(dslQOne, dslQTwo))
            .build();

        Questionnaire questionnaire1NoId = new Questionnaire(null, questionnaire1.getCode(), questionnaire1.getTitle(), questionnaire1.getIndex(), questionnaire1.getDescription(), questionnaire1.getCreationTime(), questionnaire1.getLastModificationTime());
        Questionnaire questionnaire2NoId = new Questionnaire(null, questionnaire2.getCode(), questionnaire2.getTitle(), questionnaire2.getIndex(), questionnaire2.getDescription(), questionnaire2.getCreationTime(), questionnaire2.getLastModificationTime());
        when(createQuestionnairePort.persist(questionnaire1NoId, KIT_ID, CURRENT_USER_ID)).thenReturn(questionnaire1.getId());
        when(createQuestionnairePort.persist(questionnaire2NoId, KIT_ID, CURRENT_USER_ID)).thenReturn(questionnaire2.getId());

        persister.persist(context, dslModel, KIT_ID, CURRENT_USER_ID);

        Map<String, Long> questionnaires = context.get(KEY_QUESTIONNAIRES);
        assertEquals(2, questionnaires.size());
    }
}
