package org.flickit.assessment.core.application.service.assessmentinsight;

import org.flickit.assessment.common.application.MessageBundle;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.common.exception.ValidationException;
import org.flickit.assessment.core.application.domain.AssessmentInsight;
import org.flickit.assessment.core.application.port.in.assessmentinsight.InitAssessmentInsightUseCase;
import org.flickit.assessment.core.application.port.out.assessment.GetAssessmentProgressPort;
import org.flickit.assessment.core.application.port.out.assessmentinsight.CreateAssessmentInsightPort;
import org.flickit.assessment.core.application.port.out.assessmentinsight.LoadAssessmentInsightPort;
import org.flickit.assessment.core.application.port.out.assessmentinsight.UpdateAssessmentInsightPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.core.test.fixture.application.AssessmentInsightMother;
import org.flickit.assessment.core.test.fixture.application.AssessmentResultMother;
import org.flickit.assessment.core.test.fixture.application.MaturityLevelMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import static org.flickit.assessment.core.common.ErrorMessageKey.INIT_ASSESSMENT_INSIGHT_ASSESSMENT_RESULT_NOT_FOUND;
import static org.flickit.assessment.core.common.ErrorMessageKey.INIT_ASSESSMENT_INSIGHT_INSIGHT_DUPLICATE;
import static org.flickit.assessment.core.common.MessageKey.ASSESSMENT_DEFAULT_INSIGHT_DEFAULT_COMPLETED;
import static org.flickit.assessment.core.common.MessageKey.ASSESSMENT_DEFAULT_INSIGHT_DEFAULT_INCOMPLETE;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InitAssessmentInsightServiceTest {

    @InjectMocks
    private InitAssessmentInsightService service;

    @Mock
    private LoadAssessmentResultPort loadAssessmentResultPort;

    @Mock
    private LoadAssessmentInsightPort loadAssessmentInsightPort;

    @Mock
    private GetAssessmentProgressPort getAssessmentProgressPort;

    @Mock
    private CreateAssessmentInsightPort createAssessmentInsightPort;

    @Mock
    private UpdateAssessmentInsightPort updateAssessmentInsightPort;

    private final int questionsCount = 15;
    private final double confidenceValue = 93.2;

    @Test
    void testInitAssessmentInsight_assessmentResultNotFound_throwsResourceNotFoundException() {
        var param = createParam(InitAssessmentInsightUseCase.Param.ParamBuilder::build);

        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.empty());

        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.initAssessmentInsight(param));
        assertEquals(INIT_ASSESSMENT_INSIGHT_ASSESSMENT_RESULT_NOT_FOUND, throwable.getMessage());
    }

    @Test
    void testInitAssessmentInsight_assessmentInsightByAssessorFound_throwsValidationException() {
        var param = createParam(InitAssessmentInsightUseCase.Param.ParamBuilder::build);
        var assessmentResult = AssessmentResultMother.validResultWithSubjectValuesAndMaturityLevelAndConfidenceValue(null, MaturityLevelMother.levelFive(), confidenceValue);

        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        when(loadAssessmentInsightPort.loadByAssessmentResultId(assessmentResult.getId())).thenReturn(Optional.of(AssessmentInsightMother.createSimpleAssessmentInsight()));

        var throwable = assertThrows(ValidationException.class, () -> service.initAssessmentInsight(param));
        assertEquals(INIT_ASSESSMENT_INSIGHT_INSIGHT_DUPLICATE, throwable.getMessageKey());
    }

    @Test
    void testInitAssessmentInsight_completeAssessment_successfulInitialization() {
        final var answerCount = 15;
        var param = createParam(InitAssessmentInsightUseCase.Param.ParamBuilder::build);
        var assessmentResult = AssessmentResultMother.validResultWithSubjectValuesAndMaturityLevelAndConfidenceValue(null, MaturityLevelMother.levelFive(), confidenceValue);
        var progressResult = new GetAssessmentProgressPort.Result(UUID.randomUUID(), answerCount, questionsCount);
        var insight = MessageBundle.message(ASSESSMENT_DEFAULT_INSIGHT_DEFAULT_COMPLETED, assessmentResult.getMaturityLevel().getTitle(), questionsCount, Math.ceil(confidenceValue));

        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        when(loadAssessmentInsightPort.loadByAssessmentResultId(assessmentResult.getId())).thenReturn(Optional.empty());
        when(getAssessmentProgressPort.getProgress(assessmentResult.getAssessment().getId())).thenReturn(progressResult);

        service.initAssessmentInsight(param);
        ArgumentCaptor<AssessmentInsight> createCaptor = ArgumentCaptor.forClass(AssessmentInsight.class);
        verify(createAssessmentInsightPort).persist(createCaptor.capture());
        assertNull(createCaptor.getValue().getId());
        assertEquals(assessmentResult.getId(), createCaptor.getValue().getAssessmentResultId());
        assertNull(createCaptor.getValue().getInsightBy());
        assertNotNull(createCaptor.getValue().getInsightTime());
        assertEquals(insight, createCaptor.getValue().getInsight());

        verifyNoInteractions(updateAssessmentInsightPort);
    }

    @Test
    void testInitAssessmentInsight_completeAssessmentAndInitialInsightFound_successfulReinitialization() {
        final var answerCount = 15;
        var param = createParam(InitAssessmentInsightUseCase.Param.ParamBuilder::build);
        var assessmentResult = AssessmentResultMother.validResultWithSubjectValuesAndMaturityLevelAndConfidenceValue(null, MaturityLevelMother.levelFive(), confidenceValue);
        var progressResult = new GetAssessmentProgressPort.Result(UUID.randomUUID(), answerCount, questionsCount);
        var assessmentInsight = AssessmentInsightMother.createInitialInsightWithAssessmentResultId(assessmentResult.getId());
        var insight = MessageBundle.message(ASSESSMENT_DEFAULT_INSIGHT_DEFAULT_COMPLETED, assessmentResult.getMaturityLevel().getTitle(), questionsCount, Math.ceil(confidenceValue));

        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        when(loadAssessmentInsightPort.loadByAssessmentResultId(assessmentResult.getId())).thenReturn(Optional.of(assessmentInsight));
        when(getAssessmentProgressPort.getProgress(assessmentResult.getAssessment().getId())).thenReturn(progressResult);

        service.initAssessmentInsight(param);
        ArgumentCaptor<AssessmentInsight> createCaptor = ArgumentCaptor.forClass(AssessmentInsight.class);
        verify(updateAssessmentInsightPort).updateInsight(createCaptor.capture());
        assertEquals(assessmentInsight.getId(), createCaptor.getValue().getId());
        assertEquals(assessmentResult.getId(), createCaptor.getValue().getAssessmentResultId());
        assertNull(createCaptor.getValue().getInsightBy());
        assertNotNull(createCaptor.getValue().getInsightTime());
        assertEquals(insight, createCaptor.getValue().getInsight());

        verifyNoInteractions(createAssessmentInsightPort);
    }

    @Test
    void testInitAssessmentInsight_confidenceValueIsNull_successfulInitialization() {
        var param = createParam(InitAssessmentInsightUseCase.Param.ParamBuilder::build);
        var answerCount = 13;
        var assessmentResult = AssessmentResultMother.validResultWithSubjectValuesAndMaturityLevel(null, MaturityLevelMother.levelFive());
        var assessmentInsight = AssessmentInsightMother.createInitialInsightWithAssessmentResultId(assessmentResult.getId());
        var progressResult = new GetAssessmentProgressPort.Result(UUID.randomUUID(), answerCount, questionsCount);
        var expectedInsight = MessageBundle.message(ASSESSMENT_DEFAULT_INSIGHT_DEFAULT_INCOMPLETE, assessmentResult.getMaturityLevel().getTitle(),
            answerCount, questionsCount, 0);

        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        when(loadAssessmentInsightPort.loadByAssessmentResultId(assessmentResult.getId())).thenReturn(Optional.of(assessmentInsight));
        when(getAssessmentProgressPort.getProgress(assessmentResult.getAssessment().getId())).thenReturn(progressResult);

        service.initAssessmentInsight(param);
        ArgumentCaptor<AssessmentInsight> createCaptor = ArgumentCaptor.forClass(AssessmentInsight.class);
        verify(updateAssessmentInsightPort).updateInsight(createCaptor.capture());
        assertEquals(assessmentInsight.getId(), createCaptor.getValue().getId());
        assertEquals(assessmentResult.getId(), createCaptor.getValue().getAssessmentResultId());
        assertNull(createCaptor.getValue().getInsightBy());
        assertNotNull(createCaptor.getValue().getInsightTime());
        assertEquals(expectedInsight, createCaptor.getValue().getInsight());

        verifyNoInteractions(createAssessmentInsightPort);
    }

    private InitAssessmentInsightUseCase.Param createParam(Consumer<InitAssessmentInsightUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private InitAssessmentInsightUseCase.Param.ParamBuilder paramBuilder() {
        return InitAssessmentInsightUseCase.Param.builder()
            .assessmentId(UUID.randomUUID())
            .currentUserId(UUID.randomUUID());
    }
}
