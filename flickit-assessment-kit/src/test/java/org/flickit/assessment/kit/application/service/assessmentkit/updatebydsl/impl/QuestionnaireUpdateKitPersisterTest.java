package org.flickit.assessment.kit.application.service.assessmentkit.updatebydsl.impl;

import org.flickit.assessment.kit.application.domain.AssessmentKit;
import org.flickit.assessment.kit.application.domain.Questionnaire;
import org.flickit.assessment.kit.application.domain.dsl.AssessmentKitDslModel;
import org.flickit.assessment.kit.application.domain.dsl.QuestionnaireDslModel;
import org.flickit.assessment.kit.application.port.out.questionnaire.CreateQuestionnairePort;
import org.flickit.assessment.kit.application.port.out.questionnaire.UpdateQuestionnairePort;
import org.flickit.assessment.kit.application.service.assessmentkit.updatebydsl.UpdateKitPersisterContext;
import org.flickit.assessment.kit.application.service.assessmentkit.updatebydsl.UpdateKitPersisterResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.flickit.assessment.kit.application.service.assessmentkit.updatebydsl.UpdateKitPersisterContext.KEY_QUESTIONNAIRES;
import static org.flickit.assessment.kit.test.fixture.application.AssessmentKitMother.kitWithQuestionnaires;
import static org.flickit.assessment.kit.test.fixture.application.QuestionnaireMother.questionnaireWithTitle;
import static org.flickit.assessment.kit.test.fixture.application.dsl.QuestionnaireDslModelMother.domainToDslModel;
import static org.junit.jupiter.api.Assertions.*;
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
    void testOrder() {
        assertEquals(3, persister.order());
    }

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

        UpdateKitPersisterContext ctx = new UpdateKitPersisterContext();
        UpdateKitPersisterResult result = persister.persist(ctx, savedKit, dslKit, UUID.randomUUID());

        assertFalse(result.isMajorUpdate());
        Map<String, Long> codeToIdMap = ctx.get(KEY_QUESTIONNAIRES);
        assertNotNull(codeToIdMap);
        assertEquals(2, codeToIdMap.size());

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

        when(createQuestionnairePort.persist(any(Questionnaire.class), eq(savedKit.getActiveVersionId()), any(UUID.class))).thenReturn(1L);

        UpdateKitPersisterContext ctx = new UpdateKitPersisterContext();
        UpdateKitPersisterResult result = persister.persist(ctx, savedKit, dslKit, UUID.randomUUID());

        var createPortArgument = ArgumentCaptor.forClass(Questionnaire.class);
        verify(createQuestionnairePort, times(1)).persist(createPortArgument.capture(), anyLong(), any(UUID.class));
        assertEquals(newQuestionnaire.getIndex(), createPortArgument.getValue().getIndex());
        assertEquals(newQuestionnaire.getTitle(), createPortArgument.getValue().getTitle());
        assertEquals(newQuestionnaire.getDescription(), createPortArgument.getValue().getDescription());
        assertNull(createPortArgument.getValue().getTranslations());

        assertTrue(result.isMajorUpdate());
        Map<String, Long> codeToIdMap = ctx.get(KEY_QUESTIONNAIRES);
        assertNotNull(codeToIdMap);
        assertEquals(2, codeToIdMap.size());

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

        UpdateKitPersisterContext ctx = new UpdateKitPersisterContext();
        UpdateKitPersisterResult result = persister.persist(ctx, savedKit, dslKit, UUID.randomUUID());

        var updatePortParam = ArgumentCaptor.forClass(UpdateQuestionnairePort.Param.class);
        verify(updateQuestionnairePort).update(updatePortParam.capture());
        assertNotNull(updatePortParam.getValue());
        assertEquals(savedQuestionnaire2.getId(), updatePortParam.getValue().id());
        assertEquals(savedKit.getActiveVersionId(), updatePortParam.getValue().kitVersionId());
        assertEquals(dslQTwo.getTitle(), updatePortParam.getValue().title());
        assertEquals(dslQTwo.getIndex(), updatePortParam.getValue().index());
        assertEquals(dslQTwo.getDescription(), updatePortParam.getValue().description());
        assertEquals(savedQuestionnaire2.getTranslations(), updatePortParam.getValue().translations());
        assertNotNull(updatePortParam.getValue().lastModifiedBy());
        assertNotNull(updatePortParam.getValue().lastModificationTime());

        assertFalse(result.isMajorUpdate());
        Map<String, Long> codeToIdMap = ctx.get(KEY_QUESTIONNAIRES);
        assertNotNull(codeToIdMap);
        assertEquals(2, codeToIdMap.size());

        verifyNoInteractions(createQuestionnairePort);
    }
}
