package org.flickit.assessment.users.application.port.in.space;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.users.common.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GetSpaceListUseCaseParamTest {

    @Test
    void testGetSpaceList_sizeIsLessThanMin_ErrorMessage() {
        UUID currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GetSpaceListUseCase.Param(0, 10, currentUserId));
        assertThat(throwable).hasMessage("size: " + GET_SPACE_LIST_SIZE_MIN);
    }

    @Test
    void testGetSpaceList_sizeIsMoreThenMax_ErrorMessage() {
        UUID currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GetSpaceListUseCase.Param(101, 0, currentUserId));
        assertThat(throwable).hasMessage("size: " + GET_SPACE_LIST_SIZE_MAX);
    }

    @Test
    void testGetSpaceList_pageIsLessThanZero_ErrorMessage() {
        UUID currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GetSpaceListUseCase.Param(10, -1, currentUserId));
        assertThat(throwable).hasMessage("page: " + GET_SPACE_LIST_PAGE_MIN);
    }

    @Test
    void testGetSpaceList_currentUserIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GetSpaceListUseCase.Param(10, 0, null));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }
}
