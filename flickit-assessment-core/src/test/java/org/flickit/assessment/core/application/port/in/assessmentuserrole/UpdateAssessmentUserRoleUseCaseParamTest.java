package org.flickit.assessment.core.application.port.in.assessmentuserrole;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UpdateAssessmentUserRoleUseCaseParamTest {

    @Test
    void testUpdateAssessmentUserRoleParam_assessmentIdIsNull_ErrorMessage() {
        UUID userId = UUID.randomUUID();
        UUID currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new UpdateUserAssessmentRoleUseCase.Param(null, userId, 1, currentUserId));
        assertThat(throwable).hasMessage("assessmentId: " + UPDATE_ASSESSMENT_USER_ROLE_ASSESSMENT_ID_NOT_NULL);
    }

    @Test
    void testUpdateAssessmentUserRoleParam_userIdIsNull_ErrorMessage() {
        UUID assessmentId = UUID.randomUUID();
        UUID currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new UpdateUserAssessmentRoleUseCase.Param(assessmentId, null, 1, currentUserId));
        assertThat(throwable).hasMessage("userId: " + UPDATE_ASSESSMENT_USER_ROLE_USER_ID_NOT_NULL);
    }

    @Test
    void testUpdateAssessmentUserRoleParam_roleIdIsNull_ErrorMessage() {
        UUID assessmentId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new UpdateUserAssessmentRoleUseCase.Param(assessmentId, userId, null, currentUserId));
        assertThat(throwable).hasMessage("roleId: " + UPDATE_ASSESSMENT_USER_ROLE_ROLE_ID_NOT_NULL);
    }

    @Test
    void testUpdateAssessmentUserRoleParam_currentUserIdIsNull_ErrorMessage() {
        UUID assessmentId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new UpdateUserAssessmentRoleUseCase.Param(assessmentId, userId, 1, null));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }
}
