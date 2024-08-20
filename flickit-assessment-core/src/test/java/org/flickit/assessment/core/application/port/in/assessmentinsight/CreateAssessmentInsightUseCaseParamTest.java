package org.flickit.assessment.core.application.port.in.assessmentinsight;

import jakarta.validation.ConstraintViolationException;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.*;

class CreateAssessmentInsightUseCaseParamTest {

    @Test
    void testCreateAssessmentInsightParam_AssessmentIdIsNull_ErrorMessage() {
        UUID currentUserId = UUID.randomUUID();
        String insight = RandomStringUtils.random(100);
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new CreateAssessmentInsightUseCase.Param(null, insight, currentUserId));
        assertThat(throwable).hasMessage("assessmentId: " + CREATE_ASSESSMENT_INSIGHT_ASSESSMENT_ID_NOT_NULL);
    }

    @Test
    void testCreateAssessmentInsightParam_InsightIsNull_ErrorMessage() {
        UUID assessmentId = UUID.randomUUID();
        UUID currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new CreateAssessmentInsightUseCase.Param(assessmentId, null, currentUserId));
        assertThat(throwable).hasMessage("insight: " + CREATE_ASSESSMENT_INSIGHT_INSIGHT_NOT_NULL);
    }

    @Test
    void testCreateAssessmentInsightParam_InsightIsLessThanMin_ErrorMessage() {
        UUID assessmentId = UUID.randomUUID();
        UUID currentUserId = UUID.randomUUID();
        String insight = "   ab  ";
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new CreateAssessmentInsightUseCase.Param(assessmentId, insight, currentUserId));
        assertThat(throwable).hasMessage("insight: " + CREATE_ASSESSMENT_INSIGHT_INSIGHT_SIZE_MIN);
    }

    @Test
    void testCreateAssessmentInsightParam_InsightIsMoreThanMax_ErrorMessage() {
        UUID assessmentId = UUID.randomUUID();
        UUID currentUserId = UUID.randomUUID();
        String insight = RandomStringUtils.random(1001);
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new CreateAssessmentInsightUseCase.Param(assessmentId, insight, currentUserId));
        assertThat(throwable).hasMessage("insight: " + CREATE_ASSESSMENT_INSIGHT_INSIGHT_SIZE_MAX);
    }

    @Test
    void testCreateAssessmentInsightParam_CurrentUserIdIsNull_ErrorMessage() {
        UUID assessmentId = UUID.randomUUID();
        String insight = RandomStringUtils.random(100);
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new CreateAssessmentInsightUseCase.Param(assessmentId, insight, null));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }
}
