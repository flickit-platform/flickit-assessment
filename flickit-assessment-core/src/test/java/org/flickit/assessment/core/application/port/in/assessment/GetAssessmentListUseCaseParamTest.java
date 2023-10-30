package org.flickit.assessment.core.application.port.in.assessment;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.core.common.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class GetAssessmentListUseCaseParamTest {

    @Test
    void testGetAssessmentList_NullSpaceIds_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GetAssessmentListUseCase.Param(
                null,
                null,
                20,
                0
            ));
        assertThat(throwable).hasMessage("spaceIds: " + GET_ASSESSMENT_LIST_SPACE_IDS_NOT_NULL);
    }

    @Test
    void testGetAssessmentList_EmptySpaceIds_ErrorMessage() {
        List<Long> spaceIds = new ArrayList<>();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GetAssessmentListUseCase.Param(
                spaceIds,
                null,
                20,
                0
            ));
        assertThat(throwable).hasMessage("spaceIds: " + GET_ASSESSMENT_LIST_SPACE_IDS_NOT_NULL);
    }

    @Test
    void testGetAssessmentList_PageSizeIsLessThanMin_ErrorMessage() {
        List<Long> spaceIds = List.of(1L, 2L);
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GetAssessmentListUseCase.Param(
                spaceIds,
                null,
                0,
                0
            ));
        assertThat(throwable).hasMessage("size: " + GET_ASSESSMENT_LIST_SIZE_MIN);
    }

    @Test
    void testGetAssessmentList_PageSizeIsGreaterThanMax_ErrorMessage() {
        List<Long> spaceIds = List.of(1L, 2L);
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GetAssessmentListUseCase.Param(
                spaceIds,
                null,
                101,
                0
            ));
        assertThat(throwable).hasMessage("size: " + GET_ASSESSMENT_LIST_SIZE_MAX);
    }

    @Test
    void testGetAssessmentList_PageNumberIsLessThanMin_ErrorMessage() {
        List<Long> spaceIds = List.of(1L, 2L);
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GetAssessmentListUseCase.Param(
                spaceIds,
                null,
                20,
                -1
            ));
        assertThat(throwable).hasMessage("page: " + GET_ASSESSMENT_LIST_PAGE_MIN);
    }
}
