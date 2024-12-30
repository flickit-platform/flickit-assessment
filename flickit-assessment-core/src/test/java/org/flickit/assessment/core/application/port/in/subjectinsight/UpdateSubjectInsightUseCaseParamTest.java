package org.flickit.assessment.core.application.port.in.subjectinsight;

import jakarta.validation.ConstraintViolationException;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UpdateSubjectInsightUseCaseParamTest {

    @Test
    void testUpdateSubjectInsightParam_AssessmentIdIsNull_ErrorMessage() {
        UUID currentUserId = UUID.randomUUID();
        var insight = "insight";
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new UpdateSubjectInsightUseCase.Param(null, 1L, insight, currentUserId));
        assertThat(throwable).hasMessage("assessmentId: " + CREATE_SUBJECT_INSIGHT_ASSESSMENT_ID_NOT_NULL);
    }

    @Test
    void testUpdateSubjectInsightParam_SubjectIdIsNull_ErrorMessage() {
        var assessmentId = UUID.randomUUID();
        var insight = "insight";
        UUID currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new UpdateSubjectInsightUseCase.Param(assessmentId, null, insight, currentUserId));
        assertThat(throwable).hasMessage("subjectId: " + CREATE_SUBJECT_INSIGHT_SUBJECT_ID_NOT_NULL);
    }

    @Test
    void testUpdateSubjectInsightParam_InsightIsNull_ErrorMessage() {
        var assessmentId = UUID.randomUUID();
        UUID currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new UpdateSubjectInsightUseCase.Param(assessmentId, 123L, null, currentUserId));
        assertThat(throwable).hasMessage("insight: " + CREATE_SUBJECT_INSIGHT_INSIGHT_NOT_NULL);
    }

    @Test
    void testUpdateSubjectInsight_InsightSizeIsLessThanMin_ErrorMessage() {
        var assessmentId = UUID.randomUUID();
        UUID currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new UpdateSubjectInsightUseCase.Param(assessmentId, 123L, " ab ", currentUserId));
        assertThat(throwable).hasMessage("insight: " + CREATE_SUBJECT_INSIGHT_INSIGHT_SIZE_MIN);
    }

    @Test
    void testUpdateSubjectInsightParam_InsightSizeIsGreaterThanMax_ErrorMessage() {
        var assessmentId = UUID.randomUUID();
        var insight = RandomStringUtils.randomAlphabetic(1001);
        UUID currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new UpdateSubjectInsightUseCase.Param(assessmentId, 123L, insight, currentUserId));
        assertThat(throwable).hasMessage("insight: " + CREATE_SUBJECT_INSIGHT_INSIGHT_SIZE_MAX);
    }

    @Test
    void testUpdateSubjectInsightParam_CurrentUserIdIsNull_ErrorMessage() {
        var assessmentId = UUID.randomUUID();
        var insight = "insight";
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new UpdateSubjectInsightUseCase.Param(assessmentId, 123L, insight, null));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }
}
