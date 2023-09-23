package org.flickit.flickitassessmentcore.application.port.in.assessment;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.flickitassessmentcore.common.ErrorMessageKey.CHECK_COMPARATIVE_ASSESSMENTS_ASSESSMENT_LIST_NOT_NULL;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class CheckComparativeAssessmentsUseCaseParamTest {

    @Test
    void testCheckComparativeAssessments_EmptyList_ErrorMessage() {
        var assessmentsIdsList = new ArrayList<UUID>();
        var throwable = assertThrows(ConstraintViolationException.class, () ->
            new CheckComparativeAssessmentsUseCase.Param(assessmentsIdsList));
        assertThat(throwable).hasMessage("assessmentIds: " + CHECK_COMPARATIVE_ASSESSMENTS_ASSESSMENT_LIST_NOT_NULL);
    }

    @Test
    void testCheckComparativeAssessments_NullList_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class, () ->
            new CheckComparativeAssessmentsUseCase.Param(null));
        assertThat(throwable).hasMessage("assessmentIds: " + CHECK_COMPARATIVE_ASSESSMENTS_ASSESSMENT_LIST_NOT_NULL);
    }
}
