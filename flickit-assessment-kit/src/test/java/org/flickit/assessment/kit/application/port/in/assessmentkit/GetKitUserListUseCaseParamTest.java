package org.flickit.assessment.kit.application.port.in.assessmentkit;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class GetKitUserListUseCaseParamTest {

    @Test
    void testGetKitUserList_kitIdIsNull_ErrorMessage() {
        UUID currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GetKitUserListUseCase.Param(null, 0, 10, currentUserId));
        assertThat(throwable).hasMessage("kitId: " + GET_KIT_USER_LIST_KIT_ID_NOT_NULL);
    }

    @Test
    void testGetKitUserList_PageIsLessThanMin_ErrorMessage() {
        UUID currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GetKitUserListUseCase.Param(1L, -1, 10, currentUserId));
        assertThat(throwable).hasMessage("page: " + GET_KIT_USER_LIST_PAGE_MIN);
    }

    @Test
    void testGetKitUserList_SizeIsLessThanMin_ErrorMessage() {
        UUID currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GetKitUserListUseCase.Param(1L, 0, -1, currentUserId));
        assertThat(throwable).hasMessage("size: " + GET_KIT_USER_LIST_SIZE_MIN);
    }

    @Test
    void testGetKitUserList_SizeIsMoreThanMax_ErrorMessage() {
        UUID currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GetKitUserListUseCase.Param(1L, 0, 101, currentUserId));
        assertThat(throwable).hasMessage("size: " + GET_KIT_USER_LIST_SIZE_MAX);
    }

    @Test
    void testGetKitUserList_currentUserIdIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GetKitUserListUseCase.Param(1L, 0, 10, null));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }
}
