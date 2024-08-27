package org.flickit.assessment.core.application.port.in.assessmentinsight;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.GET_ASSESSMENT_INSIGHT_ASSESSMENT_ID_NOT_NULL;
import static org.junit.jupiter.api.Assertions.*;

class GetAssessmentInsightUseCaseParamTest {

    @Test
    void testLoadAssessmentInsightParam_AssessmentIdIsNull_ErrorMessage() {
        UUID currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GetAssessmentInsightUseCase.Param(null, currentUserId));
        assertThat(throwable).hasMessage("assessmentId: " + GET_ASSESSMENT_INSIGHT_ASSESSMENT_ID_NOT_NULL);
    }

    @Test
    void testLoadAssessmentInsightParam_CurrentUserIdIsNull_ErrorMessage() {
        UUID assessmentId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GetAssessmentInsightUseCase.Param(assessmentId, null));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }

    @Test
    void testLoadAssessmentInsightParam_ValidParameters_Successful() {
        UUID assessmentId = UUID.randomUUID();
        UUID currentUserId = UUID.randomUUID();
        assertDoesNotThrow(() -> new GetAssessmentInsightUseCase.Param(assessmentId, currentUserId));
    }
}
