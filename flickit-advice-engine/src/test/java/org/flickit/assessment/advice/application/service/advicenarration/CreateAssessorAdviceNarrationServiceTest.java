package org.flickit.assessment.advice.application.service.advicenarration;

import org.apache.commons.lang3.RandomStringUtils;
import org.flickit.assessment.advice.application.domain.AdviceNarration;
import org.flickit.assessment.advice.application.domain.AssessmentResult;
import org.flickit.assessment.advice.application.port.in.advicenarration.CreateAssessorAdviceNarrationUseCase;
import org.flickit.assessment.advice.application.port.out.advicenarration.CreateAdviceNarrationPort;
import org.flickit.assessment.advice.application.port.out.advicenarration.LoadAdviceNarrationPort;
import org.flickit.assessment.advice.application.port.out.advicenarration.UpdateAdviceNarrationPort;
import org.flickit.assessment.advice.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.application.port.out.ValidateAssessmentResultPort;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.flickit.assessment.advice.common.ErrorMessageKey.CREATE_ASSESSOR_ADVICE_NARRATION_ASSESSMENT_RESULT_NOT_FOUND;
import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.CREATE_ADVICE;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateAssessorAdviceNarrationServiceTest {

    @InjectMocks
    private CreateAssessorAdviceNarrationService service;

    @Mock
    private AssessmentAccessChecker assessmentAccessChecker;

    @Mock
    private LoadAssessmentResultPort loadAssessmentResultPort;

    @Mock
    private LoadAdviceNarrationPort loadAdviceNarrationPort;

    @Mock
    private ValidateAssessmentResultPort validateAssessmentResultPort;

    @Mock
    private CreateAdviceNarrationPort createAdviceNarrationPort;

    @Mock
    private UpdateAdviceNarrationPort updateAdviceNarrationPort;

    @Test
    void testCreateAssessorAdviceNarration_WhenCurrentUserDoesNotHaveRequiredPermission_ThenThrowAccessDeniedException() {
        UUID assessmentId = UUID.randomUUID();
        UUID currentUserId = UUID.randomUUID();
        String assessorNarration = RandomStringUtils.randomAlphabetic(100);
        var param = new CreateAssessorAdviceNarrationUseCase.Param(assessmentId, assessorNarration, currentUserId);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_ADVICE)).thenReturn(false);

        var accessDeniedException = assertThrows(AccessDeniedException.class, () -> service.createAssessorAdviceNarration(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, accessDeniedException.getMessage());
    }

    @Test
    void testCreateAssessorAdviceNarration_WhenAssessmentResultDoesNotNotExist_ThenThrowResourceNotFoundException() {
        UUID assessmentId = UUID.randomUUID();
        UUID currentUserId = UUID.randomUUID();
        String assessorNarration = RandomStringUtils.randomAlphabetic(100);
        var param = new CreateAssessorAdviceNarrationUseCase.Param(assessmentId, assessorNarration, currentUserId);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_ADVICE)).thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(assessmentId)).thenReturn(Optional.empty());

        var resourceNotFoundException = assertThrows(ResourceNotFoundException.class, () -> service.createAssessorAdviceNarration(param));
        assertEquals(CREATE_ASSESSOR_ADVICE_NARRATION_ASSESSMENT_RESULT_NOT_FOUND, resourceNotFoundException.getMessage());
    }

    @Test
    void testCreateAssessorAdviceNarration_WhenAdviceNarrationDoesNotExist_ThenCreateNewOne() {
        UUID assessmentId = UUID.randomUUID();
        UUID currentUserId = UUID.randomUUID();
        String assessorNarration = RandomStringUtils.randomAlphabetic(100);
        var param = new CreateAssessorAdviceNarrationUseCase.Param(assessmentId, assessorNarration, currentUserId);
        UUID assessmentResultId = UUID.randomUUID();
        AssessmentResult assessmentResult = new AssessmentResult(assessmentResultId);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_ADVICE)).thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(assessmentId)).thenReturn(Optional.of(assessmentResult));
        doNothing().when(validateAssessmentResultPort).validate(assessmentId);
        when(loadAdviceNarrationPort.loadByAssessmentResultId(assessmentResultId)).thenReturn(Optional.empty());
        doNothing().when(createAdviceNarrationPort).persist(any(AdviceNarration.class));

        service.createAssessorAdviceNarration(param);

        verifyNoInteractions(updateAdviceNarrationPort);
    }

    @Test
    void testCreateAssessorAdviceNarration_WhenAdviceExists_ThenUpdateItsAssessorNarration() {
        UUID assessmentId = UUID.randomUUID();
        UUID currentUserId = UUID.randomUUID();
        String assessorNarration = RandomStringUtils.randomAlphabetic(100);
        var param = new CreateAssessorAdviceNarrationUseCase.Param(assessmentId, assessorNarration, currentUserId);
        UUID assessmentResultId = UUID.randomUUID();
        AssessmentResult assessmentResult = new AssessmentResult(assessmentResultId);
        AdviceNarration adviceNarration = new AdviceNarration(null,
            assessmentResultId,
            "aiNarration",
            null,
            LocalDateTime.now(),
            null,
            currentUserId);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_ADVICE)).thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(assessmentId)).thenReturn(Optional.of(assessmentResult));
        doNothing().when(validateAssessmentResultPort).validate(assessmentId);
        when(loadAdviceNarrationPort.loadByAssessmentResultId(assessmentResult.getId())).thenReturn(Optional.of(adviceNarration));
        doNothing().when(updateAdviceNarrationPort).updateAssessorNarration(any(AdviceNarration.class));

        service.createAssessorAdviceNarration(param);

        verifyNoInteractions(createAdviceNarrationPort);
    }
}
