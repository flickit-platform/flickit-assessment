package org.flickit.assessment.core.application.port.in.assessmentuserrole;

import jakarta.validation.ConstraintViolationException;
import org.flickit.assessment.core.application.domain.AssessmentUserRole;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GrantUserAssessmentRoleUseCaseParamTest {

    @Test
    void testGrantUserAssessmentRoleParam_assessmentIdIsNull_ErrorMessage() {
        UUID userId = UUID.randomUUID();
        UUID currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GrantUserAssessmentRoleUseCase.Param(null, userId, 1, currentUserId));
        assertThat(throwable).hasMessage("assessmentId: " + GRANT_ASSESSMENT_USER_ROLE_ASSESSMENT_ID_NOT_NULL);
    }

    @Test
    void testGrantUserAssessmentRoleParam_userIdIsNull_ErrorMessage() {
        UUID assessmentId = UUID.randomUUID();
        UUID currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GrantUserAssessmentRoleUseCase.Param(assessmentId, null, 1, currentUserId));
        assertThat(throwable).hasMessage("userId: " + GRANT_ASSESSMENT_USER_ROLE_USER_ID_NOT_NULL);
    }

    @Test
    void testGrantUserAssessmentRoleParam_roleIdIsNull_ErrorMessage() {
        UUID assessmentId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GrantUserAssessmentRoleUseCase.Param(assessmentId, userId, null, currentUserId));
        assertThat(throwable).hasMessage("roleId: " + GRANT_ASSESSMENT_USER_ROLE_ROLE_ID_NOT_NULL);
    }

    @Test
    void testGrantUserAssessmentRoleParam_currentUserIdIsNull_ErrorMessage() {
        UUID assessmentId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GrantUserAssessmentRoleUseCase.Param(assessmentId, userId, 1, null));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }

    @Test
    void testGrantUserAssessmentRoleParam_roleIdIsInvalid_ErrorMessage() {
        UUID assessmentId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GrantUserAssessmentRoleUseCase.Param(assessmentId, userId, AssessmentUserRole.values().length, currentUserId));
        assertThat(throwable).hasMessage("roleIdValid: " + GRANT_ASSESSMENT_USER_ROLE_ROLE_ID_INVALID);
    }
}
