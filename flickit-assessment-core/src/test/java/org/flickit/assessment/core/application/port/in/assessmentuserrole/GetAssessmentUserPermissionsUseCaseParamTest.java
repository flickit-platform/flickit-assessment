package org.flickit.assessment.core.application.port.in.assessmentuserrole;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.GET_ASSESSMENT_USER_PERMISSIONS_ASSESSMENT_ID_NOT_NULL;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GetAssessmentUserPermissionsUseCaseParamTest {

    @Test
    void testGetAssessmentUserPermissionsUseCaseParam_AssessmentIdIsNull_ErrorMessage() {
        UUID userId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GetAssessmentUserPermissionsUseCase.Param(null, userId));
        assertThat(throwable).hasMessage("assessmentId: " + GET_ASSESSMENT_USER_PERMISSIONS_ASSESSMENT_ID_NOT_NULL);
    }

    @Test
    void testGetAssessmentUserPermissionsUseCaseParam_UserIdIsNull_ErrorMessage() {
        UUID assessmentId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GetAssessmentUserPermissionsUseCase.Param(assessmentId, null));
        assertThat(throwable).hasMessage("userId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }
}
