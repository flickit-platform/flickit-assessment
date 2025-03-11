package org.flickit.assessment.kit.application.service.assessmentkit.createbydsl.impl;

import org.flickit.assessment.kit.application.domain.Measure;
import org.flickit.assessment.kit.application.domain.dsl.AssessmentKitDslModel;
import org.flickit.assessment.kit.application.port.out.measure.CreateMeasurePort;
import org.flickit.assessment.kit.application.service.assessmentkit.createbydsl.CreateKitPersisterContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.flickit.assessment.kit.application.service.assessmentkit.createbydsl.CreateKitPersisterContext.KEY_MEASURE;
import static org.flickit.assessment.kit.test.fixture.application.QuestionnaireMother.questionnaireWithTitle;
import static org.flickit.assessment.kit.test.fixture.application.dsl.QuestionnaireDslModelMother.domainToDslModel;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MeasureCreateKitPersisterTest {

    private static final Long KIT_ID = 1L;
    private static final UUID CURRENT_USER_ID = UUID.randomUUID();

    @InjectMocks
    private MeasureCreateKitPersister persister;

    @Mock
    private CreateMeasurePort createMeasurePort;

    @Test
    void testOrder() {
        Assertions.assertEquals(4, persister.order());
    }

    @Test
    void testMeasureCreateKitPersister_whenDslQuestionnairesIsValid_thenPersistMeasuresFromQuestionnaires() {
        var dslQOne = domainToDslModel(questionnaireWithTitle("Clean Architecture"));
        var dslQTwo = domainToDslModel(questionnaireWithTitle("Code Quality"));
        var dslModel = AssessmentKitDslModel.builder()
            .questionnaires(List.of(dslQOne, dslQTwo))
            .build();
        var context = new CreateKitPersisterContext();

        when(createMeasurePort.persist(any(Measure.class), eq(KIT_ID), eq(CURRENT_USER_ID)))
            .thenReturn(15L, 16L);

        persister.persist(context, dslModel, KIT_ID, CURRENT_USER_ID);

        var measureArgumentCaptor = ArgumentCaptor.forClass(Measure.class);
        verify(createMeasurePort, times(2)).persist(measureArgumentCaptor.capture(), eq(KIT_ID), eq(CURRENT_USER_ID));

        assertEquals(dslQOne.getCode(), measureArgumentCaptor.getAllValues().getFirst().getCode());
        assertEquals(dslQOne.getTitle(), measureArgumentCaptor.getAllValues().getFirst().getTitle());
        assertEquals(dslQOne.getIndex(), measureArgumentCaptor.getAllValues().getFirst().getIndex());
        assertEquals(dslQOne.getDescription(), measureArgumentCaptor.getAllValues().getFirst().getDescription());
        assertNotNull(measureArgumentCaptor.getAllValues().getFirst().getCreationTime());
        assertNotNull(measureArgumentCaptor.getAllValues().getFirst().getLastModificationTime());

        assertEquals(dslQTwo.getCode(), measureArgumentCaptor.getAllValues().get(1).getCode());
        assertEquals(dslQTwo.getTitle(), measureArgumentCaptor.getAllValues().get(1).getTitle());
        assertEquals(dslQTwo.getIndex(), measureArgumentCaptor.getAllValues().get(1).getIndex());
        assertEquals(dslQTwo.getDescription(), measureArgumentCaptor.getAllValues().get(1).getDescription());
        assertNotNull(measureArgumentCaptor.getAllValues().get(1).getCreationTime());
        assertNotNull(measureArgumentCaptor.getAllValues().get(1).getLastModificationTime());

        Map<String, Long> questionnaires = context.get(KEY_MEASURE);
        assertEquals(2, questionnaires.size());
    }
}
