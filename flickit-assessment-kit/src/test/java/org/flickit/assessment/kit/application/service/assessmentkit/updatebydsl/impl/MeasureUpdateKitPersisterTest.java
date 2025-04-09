package org.flickit.assessment.kit.application.service.assessmentkit.updatebydsl.impl;

import org.flickit.assessment.kit.application.domain.Measure;
import org.flickit.assessment.kit.application.domain.dsl.AssessmentKitDslModel;
import org.flickit.assessment.kit.application.port.out.measure.CreateMeasurePort;
import org.flickit.assessment.kit.application.service.assessmentkit.updatebydsl.UpdateKitPersisterContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.flickit.assessment.kit.application.service.assessmentkit.updatebydsl.UpdateKitPersisterContext.KEY_MEASURE;
import static org.flickit.assessment.kit.test.fixture.application.AssessmentKitMother.kitWithMeasures;
import static org.flickit.assessment.kit.test.fixture.application.MeasureMother.measureFromQuestionnaire;
import static org.flickit.assessment.kit.test.fixture.application.QuestionnaireMother.questionnaireWithTitle;
import static org.flickit.assessment.kit.test.fixture.application.dsl.QuestionnaireDslModelMother.domainToDslModel;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MeasureUpdateKitPersisterTest {

    @InjectMocks
    private MeasureUpdateKitPersister persister;

    @Mock
    private CreateMeasurePort createMeasurePort;

    @Test
    void testOrder() {
        assertEquals(4, persister.order());
    }

    @Test
    void testMeasureUpdateKitPersister_whenTwoSameMeasuresAlreadyExistAsDslQuestionnaires_thenDoNothing() {
        var questionnaire1 = questionnaireWithTitle("Clean Architecture");
        var questionnaire2 = questionnaireWithTitle("Code Quality");
        var savedMeasure1 = measureFromQuestionnaire(questionnaire1);
        var savedMeasure2 = measureFromQuestionnaire(questionnaire2);
        var savedKit = kitWithMeasures(List.of(savedMeasure1, savedMeasure2));

        var dslQOne = domainToDslModel(questionnaire1);
        var dslQTwo = domainToDslModel(questionnaire2);
        var dslKit = AssessmentKitDslModel.builder()
            .questionnaires(List.of(dslQOne, dslQTwo))
            .build();

        var ctx = new UpdateKitPersisterContext();
        var result = persister.persist(ctx, savedKit, dslKit, UUID.randomUUID());

        assertFalse(result.isMajorUpdate());
        Map<String, Long> codeToIdMap = ctx.get(KEY_MEASURE);
        assertNotNull(codeToIdMap);
        assertEquals(2, codeToIdMap.keySet().size());

        verifyNoInteractions(createMeasurePort);
    }

    @Test
    void testMeasureUpdateKitPersister_whenOneNewQuestionnaireAddedToDslAndDoesNotExistInMeasuresDB_thenCreateNewMeasureFromDslQuestionnaire() {
        var questionnaire1 = questionnaireWithTitle("Clean Architecture");
        var savedMeasure1 = measureFromQuestionnaire(questionnaire1);
        var savedKit = kitWithMeasures(List.of(savedMeasure1));

        var dslQOne = domainToDslModel(questionnaire1);
        var dslQTwo = domainToDslModel(questionnaireWithTitle("Test"));

        var dslKit = AssessmentKitDslModel.builder()
            .questionnaires(List.of(dslQOne, dslQTwo))
            .build();

        when(createMeasurePort.persist(any(Measure.class), eq(savedKit.getActiveVersionId()), any(UUID.class)))
            .thenReturn(1L);

        var ctx = new UpdateKitPersisterContext();
        var result = persister.persist(ctx, savedKit, dslKit, UUID.randomUUID());

        assertTrue(result.isMajorUpdate());
        Map<String, Long> codeToIdMap = ctx.get(KEY_MEASURE);
        assertNotNull(codeToIdMap);
        assertEquals(2, codeToIdMap.keySet().size());

        var measureArgumentCaptor = ArgumentCaptor.forClass(Measure.class);
        verify(createMeasurePort, times(1))
            .persist(measureArgumentCaptor.capture(), eq(savedKit.getActiveVersionId()), any(UUID.class));

        assertEquals(dslQTwo.getCode(), measureArgumentCaptor.getValue().getCode());
        assertEquals(dslQTwo.getTitle(), measureArgumentCaptor.getValue().getTitle());
        assertEquals(dslQTwo.getIndex(), measureArgumentCaptor.getValue().getIndex());
        assertEquals(dslQTwo.getDescription(), measureArgumentCaptor.getValue().getDescription());
        assertNotNull(measureArgumentCaptor.getValue().getCreationTime());
        assertNotNull(measureArgumentCaptor.getValue().getLastModificationTime());
    }

    @Test
    void testMeasureUpdateKitPersister_whenSecondQuestionnaireTitleIsChanged_thenDoNothing() {
        var questionnaire1 = questionnaireWithTitle("Clean Architecture");
        var questionnaire2 = questionnaireWithTitle("Old Code Quality");
        var savedMeasure1 = measureFromQuestionnaire(questionnaire1);
        var savedMeasure2 = measureFromQuestionnaire(questionnaire2);
        var savedKit = kitWithMeasures(List.of(savedMeasure1, savedMeasure2));

        var dslQOne = domainToDslModel(questionnaire1);
        var dslQTwo = domainToDslModel(questionnaire2, b -> b.title("Test"));
        var dslKit = AssessmentKitDslModel.builder()
            .questionnaires(List.of(dslQOne, dslQTwo))
            .build();

        var ctx = new UpdateKitPersisterContext();
        var currentUserId = UUID.randomUUID();
        var result = persister.persist(ctx, savedKit, dslKit, currentUserId);

        assertFalse(result.isMajorUpdate());
        Map<String, Long> codeToIdMap = ctx.get(KEY_MEASURE);
        assertNotNull(codeToIdMap);
        assertEquals(2, codeToIdMap.keySet().size());

        verifyNoInteractions(createMeasurePort);
    }
}
