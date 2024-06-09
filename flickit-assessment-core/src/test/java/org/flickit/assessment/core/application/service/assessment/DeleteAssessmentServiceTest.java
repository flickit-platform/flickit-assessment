package org.flickit.assessment.core.application.service.assessment;

import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.core.application.port.in.assessment.DeleteAssessmentUseCase;
import org.flickit.assessment.core.application.port.out.assessment.DeleteAssessmentPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.DELETE_ASSESSMENT;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.core.application.service.constant.AssessmentConstants.NOT_DELETED_DELETION_TIME;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteAssessmentServiceTest {

    @InjectMocks
    private DeleteAssessmentService service;

    @Mock
    private DeleteAssessmentPort deleteAssessmentPort;

    @Mock
    private AssessmentAccessChecker assessmentAccessChecker;

    @Test
    void testDeleteAssessment_ValidAssessmentId_DeleteSuccessfully() {
        UUID assessmentId = UUID.randomUUID();
        UUID currentUserId = UUID.randomUUID();
        DeleteAssessmentUseCase.Param param = new DeleteAssessmentUseCase.Param(assessmentId, currentUserId);
        when(assessmentAccessChecker.isAuthorized(assessmentId, currentUserId, DELETE_ASSESSMENT)).thenReturn(true);
        doNothing().when(deleteAssessmentPort).deleteById(any(), any());

        service.deleteAssessment(param);

        ArgumentCaptor<UUID> portIdParam = ArgumentCaptor.forClass(UUID.class);
        ArgumentCaptor<Long> portDeletionTimeParam = ArgumentCaptor.forClass(Long.class);
        verify(deleteAssessmentPort).deleteById(portIdParam.capture(), portDeletionTimeParam.capture());

        assertEquals(assessmentId, portIdParam.getValue());
        assertThat(portDeletionTimeParam.getValue(), not(equalTo(NOT_DELETED_DELETION_TIME)));
        long now = System.currentTimeMillis();
        assertThat(portDeletionTimeParam.getValue(), lessThanOrEqualTo(now));
        verify(deleteAssessmentPort, times(1)).deleteById(any(), any());
    }

    @Test
    void testDeleteAssessment_WhenCurrentUserIdIsNull_ThenThrowsException() {
        UUID assessmentId = UUID.randomUUID();
        UUID currentUserId = UUID.randomUUID();
        DeleteAssessmentUseCase.Param param = new DeleteAssessmentUseCase.Param(assessmentId, currentUserId);
        when(assessmentAccessChecker.isAuthorized(assessmentId, currentUserId, DELETE_ASSESSMENT)).thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.deleteAssessment(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());
    }
}
