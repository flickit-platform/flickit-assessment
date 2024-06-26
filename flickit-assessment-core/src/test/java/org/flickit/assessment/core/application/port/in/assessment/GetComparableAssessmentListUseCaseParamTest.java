package org.flickit.assessment.core.application.port.in.assessment;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GetComparableAssessmentListUseCaseParamTest {

    @Test
    void testGetComparableAssessmentList_KitIdIsNull_Success() {
        UUID currentUserId = UUID.randomUUID();
        assertDoesNotThrow(() -> new GetComparableAssessmentListUseCase.Param(null, currentUserId, 1, 0));
    }

    @Test
    void testGetComparableAssessmentList_CurrentUserIdIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GetComparableAssessmentListUseCase.Param(1L, null, 1, 0));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }

    @Test
    void testGetComparableAssessmentList_PageSizeIsLessThanMin_ErrorMessage() {
        UUID currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GetComparableAssessmentListUseCase.Param(1L, currentUserId, 0, 0));
        assertThat(throwable).hasMessage("size: " + GET_ASSESSMENT_LIST_SIZE_MIN);
    }

    @Test
    void testGetComparableAssessmentList_PageSizeIsGreaterThanMax_ErrorMessage() {
        UUID currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GetComparableAssessmentListUseCase.Param(1L, currentUserId, 101, 0));
        assertThat(throwable).hasMessage("size: " + GET_ASSESSMENT_LIST_SIZE_MAX);
    }

    @Test
    void testGetComparableAssessmentList_PageNumberIsLessThanMin_ErrorMessage() {
        UUID currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GetComparableAssessmentListUseCase.Param(1L, currentUserId, 20, -1));
        assertThat(throwable).hasMessage("page: " + GET_ASSESSMENT_LIST_PAGE_MIN);
    }
}
