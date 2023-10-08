package org.flickit.flickitassessmentcore.application.port.in.assessment;

import jakarta.validation.ConstraintViolationException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.flickit.flickitassessmentcore.common.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CompareAssessmentsUseCaseParamTest {

    @Test
    void testCompareAssessments_AssessmentIdsIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new CompareAssessmentsUseCase.Param(null));
        Assertions.assertThat(throwable).hasMessage("assessmentIds: " + COMPARE_ASSESSMENTS_ASSESSMENT_IDS_NOT_NULL);
    }

    @Test
    void testCompareAssessments_AssessmentIdsSizeIsLessThanMin_ErrorMessage() {
        List<UUID> assessmentIds = List.of(UUID.randomUUID());
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new CompareAssessmentsUseCase.Param(assessmentIds));
        Assertions.assertThat(throwable).hasMessage("assessmentIds: " + COMPARE_ASSESSMENTS_ASSESSMENT_IDS_SIZE_MIN);
    }

    @Test
    void testCompareAssessments_AssessmentIdsSizeIsMoreThanMax_ErrorMessage() {
        List<UUID> assessmentIds = List.of(
            UUID.randomUUID(),
            UUID.randomUUID(),
            UUID.randomUUID(),
            UUID.randomUUID(),
            UUID.randomUUID()
        );
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new CompareAssessmentsUseCase.Param(assessmentIds));
        Assertions.assertThat(throwable).hasMessage("assessmentIds: " + COMPARE_ASSESSMENTS_ASSESSMENT_IDS_SIZE_MAX);
    }
}
