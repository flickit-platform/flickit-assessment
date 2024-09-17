package org.flickit.assessment.core.application.port.in.assessmentanalysisinsight;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.*;

class GetAssessmentAnalysisInsightUseCaseParamTest {

    @Test
    void testGetAssessmentAnalysisInsightParam_assessmentIdIsNull_ErrorMessage() {
        UUID currentUserId = UUID.randomUUID();
        ConstraintViolationException throwable = assertThrows(ConstraintViolationException.class,
            () -> new GetAssessmentAnalysisInsightUseCase.Param(null, "CODE_QUALITY", currentUserId));
        assertThat(throwable).hasMessage("assessmentId: " + GET_ASSESSMENT_ANALYSIS_INSIGHT_ASSESSMENT_ID_NOT_NULL);
    }

    @Test
    void testGetAssessmentAnalysisInsightParam_assessmentAnalysisTypeIsNull_ErrorMessage() {
        UUID assessmentId = UUID.randomUUID();
        UUID currentUserId = UUID.randomUUID();
        ConstraintViolationException throwable = assertThrows(ConstraintViolationException.class,
            () -> new GetAssessmentAnalysisInsightUseCase.Param(assessmentId, null, currentUserId));
        assertThat(throwable).hasMessage("type: " + GET_ASSESSMENT_ANALYSIS_INSIGHT_TYPE_NOT_NULL);
    }

    @Test
    void testGetAssessmentAnalysisInsightParam_assessmentAnalysisTypeIsWrong_ErrorMessage() {
        UUID assessmentId = UUID.randomUUID();
        UUID currentUserId = UUID.randomUUID();
        ConstraintViolationException throwable = assertThrows(ConstraintViolationException.class,
            () -> new GetAssessmentAnalysisInsightUseCase.Param(assessmentId, "SCAT", currentUserId));
        assertThat(throwable).hasMessage("type: " + GET_ASSESSMENT_ANALYSIS_INSIGHT_TYPE_INVALID);
    }

    @Test
    void testGetAssessmentAnalysisInsightParam_currentUserIdIsNull_ErrorMessage() {
        UUID assessmentId = UUID.randomUUID();
        ConstraintViolationException throwable = assertThrows(ConstraintViolationException.class,
            () -> new GetAssessmentAnalysisInsightUseCase.Param(assessmentId, "CODE_QUALITY", null));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }
}
