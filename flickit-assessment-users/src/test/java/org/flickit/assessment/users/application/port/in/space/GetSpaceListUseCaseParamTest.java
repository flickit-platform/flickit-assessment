package org.flickit.assessment.users.application.port.in.space;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;

import java.util.Random;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.users.common.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GetSpaceListUseCaseParamTest {

    @Test
    void testGetSpaceList_sizeIsLessThanMin_ErrorMessage() {
        int page = new Random().nextInt(100);
        UUID currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GetSpaceListUseCase.Param(0, page, currentUserId));
        assertThat(throwable).hasMessage("size: " + GET_SPACE_LIST_SIZE_MIN);
    }

    @Test
    void testGetSpaceList_sizeIsMoreThenMax_ErrorMessage() {
        int page = new Random().nextInt(100);
        UUID currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GetSpaceListUseCase.Param(101, page, currentUserId));
        assertThat(throwable).hasMessage("size: " + GET_SPACE_LIST_SIZE_MAX);
    }

    @Test
    void testGetSpaceList_pageIsLessThanZero_ErrorMessage() {
        int size = new Random().nextInt(100);
        UUID currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GetSpaceListUseCase.Param(size, -1, currentUserId));
        assertThat(throwable).hasMessage("page: " + GET_SPACE_LIST_PAGE_MIN);
    }

    @Test
    void testGetSpaceList_currentUserIsNull_ErrorMessage() {
        int size = new Random().nextInt(100);
        int page = new Random().nextInt(100);
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GetSpaceListUseCase.Param(size, page, null));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }
}
