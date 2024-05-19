package org.flickit.assessment.core.application.port.in.assessmentuserrole;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DeleteAssessmentUserRoleUseCaseParamTest {

    @Test
    void testDeleteAssessmentUserRoleParam_assessmentIdIsNull_ErrorMessage() {
        UUID userId = UUID.randomUUID();
        UUID currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new DeleteUserAssessmentRoleUseCase.Param(null, userId, currentUserId));
        assertThat(throwable).hasMessage("assessmentId: " + DELETE_ASSESSMENT_USER_ROLE_ASSESSMENT_ID_NOT_NULL);
    }

    @Test
    void testDeleteAssessmentUserRoleParam_userIdIsNull_ErrorMessage() {
        UUID assessmentId = UUID.randomUUID();
        UUID currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new DeleteUserAssessmentRoleUseCase.Param(assessmentId, null, currentUserId));
        assertThat(throwable).hasMessage("userId: " + DELETE_ASSESSMENT_USER_ROLE_USER_ID_NOT_NULL);
    }

    @Test
    void testDeleteAssessmentUserRoleParam_currentUserIdIsNull_ErrorMessage() {
        UUID assessmentId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new DeleteUserAssessmentRoleUseCase.Param(assessmentId, userId,  null));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }
}
