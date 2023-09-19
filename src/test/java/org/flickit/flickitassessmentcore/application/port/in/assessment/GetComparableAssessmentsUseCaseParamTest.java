package org.flickit.flickitassessmentcore.application.port.in.assessment;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.flickitassessmentcore.common.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class GetComparableAssessmentsUseCaseParamTest {

    @Test
    void getComparableAssessments_NullSpaceIds() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GetComparableAssessmentsUseCase.Param(
                null,
                null,
                20,
                0
            ));
        assertThat(throwable).hasMessage("spaceIds: " + GET_COMPARABLE_ASSESSMENTS_SPACE_IDS_NOT_NULL);
    }

    @Test
    void getComparableAssessments_EmptySpaceIds() {
        ArrayList<Long> spaceIds = new ArrayList<>();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GetComparableAssessmentsUseCase.Param(
                spaceIds,
                null,
                20,
                0
            ));
        assertThat(throwable).hasMessage("spaceIds: " + GET_COMPARABLE_ASSESSMENTS_SPACE_IDS_NOT_NULL);
    }

    @Test
    void getComparableAssessments_MinSize() {
        List<Long> spaceIds = List.of(1L, 2L);
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GetComparableAssessmentsUseCase.Param(
                spaceIds,
                null,
                0,
                0
            ));
        assertThat(throwable).hasMessage("size: " + GET_COMPARABLE_ASSESSMENTS_SIZE_MIN);
    }

    @Test
    void getComparableAssessments_MaxSize() {
        List<Long> spaceIds = List.of(1L, 2L);
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GetComparableAssessmentsUseCase.Param(
                spaceIds,
                null,
                101,
                0
            ));
        assertThat(throwable).hasMessage("size: " + GET_COMPARABLE_ASSESSMENTS_SIZE_MAX);
    }

    @Test
    void getComparableAssessments_MinPage() {
        List<Long> spaceIds = List.of(1L, 2L);
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GetComparableAssessmentsUseCase.Param(
                spaceIds,
                null,
                20,
                -1
            ));
        assertThat(throwable).hasMessage("page: " + GET_COMPARABLE_ASSESSMENTS_PAGE_MIN);
    }
}
