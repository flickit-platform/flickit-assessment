package org.flickit.assessment.core.application.port.in.assessmentuserrole;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.GET_ASSESSMENT_USER_PERMISSIONS_ASSESSMENT_ID_NOT_NULL;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GetAssessmentUserPermissionsUseCaseParamTest {

    private UUID assessmentId;
    private UUID userId;

    @BeforeEach
    void setUp() {
        assessmentId = UUID.randomUUID();
        userId = UUID.randomUUID();
    }

    @Test
    void testGetAssessmentUserPermissionsUseCaseParam_AssessmentIdIsNull_ErrorMessage() {
        assessmentId = null;
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GetAssessmentUserPermissionsUseCase.Param(assessmentId, userId));
        assertThat(throwable).hasMessage("assessmentId: " + GET_ASSESSMENT_USER_PERMISSIONS_ASSESSMENT_ID_NOT_NULL);
    }

    @Test
    void testGetAssessmentUserPermissionsUseCaseParam_UserIdIsNull_ErrorMessage() {
        userId = null;
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GetAssessmentUserPermissionsUseCase.Param(assessmentId, userId));
        assertThat(throwable).hasMessage("userId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }
}
