package org.flickit.assessment.advice.application.service.advice;

import org.flickit.assessment.advice.application.domain.AdviceNarration;
import org.flickit.assessment.advice.application.domain.AssessmentResult;
import org.flickit.assessment.advice.application.port.in.GetAdviceNarrationUseCase;
import org.flickit.assessment.advice.application.port.out.advicenarration.LoadAdviceNarrationPort;
import org.flickit.assessment.advice.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.common.application.MessageBundle;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.config.AppAiProperties;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.flickit.assessment.advice.common.ErrorMessageKey.GET_ADVICE_NARRATION_ASSESSMENT_RESULT_NOT_FOUND;
import static org.flickit.assessment.advice.common.MessageKey.ADVICE_NARRATION_AI_IS_DISABLED;
import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.CREATE_ADVICE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetAdviceNarrationServiceTest {

    @InjectMocks
    private GetAdviceNarrationService service;

    @Mock
    private AssessmentAccessChecker assessmentAccessChecker;

    @Mock
    private LoadAssessmentResultPort loadAssessmentResultPort;

    @Mock
    private LoadAdviceNarrationPort loadAdviceNarrationPort;

    @Mock
    private AppAiProperties appAiProperties;

    @Test
    void testGetAdviceNarration_WhenAssessmentDoesNotHaveAnyResult_ThenThrowResourceNotFoundException() {
        UUID assessmentId = UUID.randomUUID();
        UUID currentUserId = UUID.randomUUID();
        var param = new GetAdviceNarrationUseCase.Param(assessmentId, currentUserId);

        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.empty());

        ResourceNotFoundException throwable = assertThrows(ResourceNotFoundException.class, () -> service.getAdviceNarration(param));
        assertEquals(GET_ADVICE_NARRATION_ASSESSMENT_RESULT_NOT_FOUND, throwable.getMessage());
    }

    @Test
    void testGetAdviceNarration_WhenThereIsNoAdviceNarrationAndAiIsDisabled_ThenAiNarrationContainsAiIsNotEnabledMessageAndIsNotEditable() {
        UUID assessmentId = UUID.randomUUID();
        UUID currentUserId = UUID.randomUUID();
        var param = new GetAdviceNarrationUseCase.Param(assessmentId, currentUserId);
        var assessmentResult = new AssessmentResult(UUID.randomUUID());

        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(),CREATE_ADVICE)).thenReturn(false);
        when(loadAdviceNarrationPort.loadByAssessmentResultId(assessmentResult.getId())).thenReturn(Optional.empty());
        when(appAiProperties.isEnabled()).thenReturn(false);

        var result = service.getAdviceNarration(param);
        assertNotNull(result);
        assertEquals(MessageBundle.message(ADVICE_NARRATION_AI_IS_DISABLED), result.aiNarration().narration());
        assertFalse(result.editable());
        assertNull(result.aiNarration().creationTime());
        assertNull(result.assessorNarration());
    }

    @Test
    void testGetAdviceNarration_WhenThereIsNoAdviceNarrationAndAiIsEnabled_ThenAiNarrationAndAssessorNarrationAreNull() {
        UUID assessmentId = UUID.randomUUID();
        UUID currentUserId = UUID.randomUUID();
        var param = new GetAdviceNarrationUseCase.Param(assessmentId, currentUserId);
        var assessmentResult = new AssessmentResult(UUID.randomUUID());

        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(),CREATE_ADVICE)).thenReturn(true);
        when(loadAdviceNarrationPort.loadByAssessmentResultId(assessmentResult.getId())).thenReturn(Optional.empty());
        when(appAiProperties.isEnabled()).thenReturn(true);

        var result = service.getAdviceNarration(param);

        assertNotNull(result);
        assertNull(result.aiNarration());
        assertNull(result.assessorNarration());
    }

    @Test
    void testGetAdviceNarration_WhenAdviceNarrationIsNotEmptyAndJustAssessorNarrationIsNull_ThenReturnAiNarrationAsResult() {
        UUID assessmentId = UUID.randomUUID();
        UUID currentUserId = UUID.randomUUID();
        var param = new GetAdviceNarrationUseCase.Param(assessmentId, currentUserId);
        var assessmentResult = new AssessmentResult(UUID.randomUUID());
        LocalDateTime aiNarrationTime = LocalDateTime.now();
        var adviceNarration = new AdviceNarration(UUID.randomUUID(),
            assessmentResult.getId(),
            "aiNarration",
            null,
            aiNarrationTime,
            null,
            currentUserId);

        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(),CREATE_ADVICE)).thenReturn(true);
        when(loadAdviceNarrationPort.loadByAssessmentResultId(assessmentResult.getId())).thenReturn(Optional.of(adviceNarration));

        var result = service.getAdviceNarration(param);

        assertNotNull(result);
        assertNotNull(result.aiNarration());
        assertNull(result.assessorNarration());
    }

    @Test
    void testAdviceNarration_WhenAdviceNarrationIsNotEmptyAndJustAiNarrationIsNull_thenReturnAssessorNarrationAsResult() {
        UUID assessmentId = UUID.randomUUID();
        UUID currentUserId = UUID.randomUUID();
        var param = new GetAdviceNarrationUseCase.Param(assessmentId, currentUserId);
        var assessmentResult = new AssessmentResult(UUID.randomUUID());
        LocalDateTime assessorNarrationTime = LocalDateTime.now();
        var adviceNarration = new AdviceNarration(UUID.randomUUID(),
            assessmentResult.getId(),
            null,
            "assessorNarration",
            null,
            assessorNarrationTime,
            currentUserId);

        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(),CREATE_ADVICE)).thenReturn(true);
        when(loadAdviceNarrationPort.loadByAssessmentResultId(assessmentResult.getId())).thenReturn(Optional.of(adviceNarration));

        var result = service.getAdviceNarration(param);

        assertNotNull(result);
        assertNull(result.aiNarration());
        assertNotNull(result.assessorNarration());
    }
}
