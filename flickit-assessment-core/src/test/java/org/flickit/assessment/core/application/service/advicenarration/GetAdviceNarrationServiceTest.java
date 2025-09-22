package org.flickit.assessment.core.application.service.advicenarration;

import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.config.AppAiProperties;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.port.in.advicenarration.GetAdviceNarrationUseCase;
import org.flickit.assessment.core.application.port.out.advicenarration.LoadAdviceNarrationPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.core.test.fixture.application.AssessmentResultMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.CREATE_ADVICE;
import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.VIEW_ASSESSMENT_REPORT;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.core.common.ErrorMessageKey.GET_ADVICE_NARRATION_ASSESSMENT_RESULT_NOT_FOUND;
import static org.flickit.assessment.core.test.fixture.application.AdviceNarrationMother.aiAdviceNarrationWithTime;
import static org.flickit.assessment.core.test.fixture.application.AdviceNarrationMother.assessorAdviceNarrationWithTime;
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
    void testGetAdviceNarration_whenCurrentUserDoesNotHaveRequiredPermission_thenThrowAccessDeniedException() {
        var param = new GetAdviceNarrationUseCase.Param(UUID.randomUUID(), UUID.randomUUID());

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ASSESSMENT_REPORT)).thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.getAdviceNarration(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());
    }

    @Test
    void testGetAdviceNarration_whenAssessmentResultNotExists_thenThrowResourceNotFoundException() {
        UUID assessmentId = UUID.randomUUID();
        UUID currentUserId = UUID.randomUUID();
        var param = new GetAdviceNarrationUseCase.Param(assessmentId, currentUserId);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ASSESSMENT_REPORT)).thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.empty());

        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.getAdviceNarration(param));
        assertEquals(GET_ADVICE_NARRATION_ASSESSMENT_RESULT_NOT_FOUND, throwable.getMessage());
    }

    @Test
    void testGetAdviceNarration_whenThereIsNoAdviceNarration_thenAiNarrationAndAssessorNarrationAreNull() {
        UUID assessmentId = UUID.randomUUID();
        UUID currentUserId = UUID.randomUUID();
        var param = new GetAdviceNarrationUseCase.Param(assessmentId, currentUserId);
        var assessmentResult = AssessmentResultMother.validResult();

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ASSESSMENT_REPORT)).thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_ADVICE)).thenReturn(true);
        when(loadAdviceNarrationPort.loadByAssessmentResultId(assessmentResult.getId())).thenReturn(Optional.empty());
        when(appAiProperties.isEnabled()).thenReturn(true);

        var result = service.getAdviceNarration(param);

        assertNotNull(result);
        assertNull(result.aiNarration());
        assertNull(result.assessorNarration());
        assertTrue(result.editable());
        assertTrue(result.aiEnabled());

        assertTrue(result.issues().notGenerated());
        assertFalse(result.issues().unapproved());
        assertFalse(result.issues().expired());
    }

    @Test
    void testGetAdviceNarration_whenAdviceNarrationExistsAndCreatedByIsNull_ThenReturnAiNarrationAsResult() {
        UUID assessmentId = UUID.randomUUID();
        UUID currentUserId = UUID.randomUUID();
        var param = new GetAdviceNarrationUseCase.Param(assessmentId, currentUserId);
        var assessmentResult = AssessmentResultMother.validResult();
        var adviceNarration = aiAdviceNarrationWithTime(LocalDateTime.MAX);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ASSESSMENT_REPORT)).thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_ADVICE)).thenReturn(true);
        when(loadAdviceNarrationPort.loadByAssessmentResultId(assessmentResult.getId())).thenReturn(Optional.of(adviceNarration));
        when(appAiProperties.isEnabled()).thenReturn(false);

        var result = service.getAdviceNarration(param);

        assertNotNull(result);
        assertNotNull(result.aiNarration());
        assertEquals(adviceNarration.getAiNarration(), result.aiNarration().narration());
        assertEquals(adviceNarration.getAiNarrationTime(), result.aiNarration().creationTime());
        assertNull(result.assessorNarration());
        assertTrue(result.editable());
        assertFalse(result.aiEnabled());

        assertFalse(result.issues().notGenerated());
        assertTrue(result.issues().unapproved());
        assertFalse(result.issues().expired());
    }

    @Test
    void testGetAdviceNarration_whenAdviceNarrationExistsAndCreatedByIsNotNull_thenReturnAssessorNarrationAsResult() {
        UUID assessmentId = UUID.randomUUID();
        UUID currentUserId = UUID.randomUUID();
        var param = new GetAdviceNarrationUseCase.Param(assessmentId, currentUserId);
        var assessmentResult = AssessmentResultMother.validResult();
        var adviceNarration = assessorAdviceNarrationWithTime(LocalDateTime.MIN);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ASSESSMENT_REPORT)).thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_ADVICE)).thenReturn(false);
        when(loadAdviceNarrationPort.loadByAssessmentResultId(assessmentResult.getId())).thenReturn(Optional.of(adviceNarration));
        when(appAiProperties.isEnabled()).thenReturn(true);

        var result = service.getAdviceNarration(param);

        assertNotNull(result);
        assertNull(result.aiNarration());
        assertNotNull(result.assessorNarration());
        assertEquals(adviceNarration.getAssessorNarration(), result.assessorNarration().narration());
        assertEquals(adviceNarration.getAssessorNarrationTime(), result.assessorNarration().creationTime());
        assertFalse(result.editable());
        assertTrue(result.aiEnabled());

        assertFalse(result.issues().notGenerated());
        assertFalse(result.issues().unapproved());
        assertTrue(result.issues().expired());
    }
}
