package org.flickit.assessment.kit.application.port.in.assessmentkit;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.kit.common.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class GetKitUserListUseCaseParamTest {

    @Test
    void testGetKitUserList_kitIdIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GetKitUserListUseCase.Param(null, 0, 10));
        assertThat(throwable).hasMessage("kitId: " + GET_KIT_USER_LIST_KIT_ID_NOT_NULL);
    }

    @Test
    void testGetKitUserList_PageIsLessThanMin_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GetKitUserListUseCase.Param(1L, -1, 10));
        assertThat(throwable).hasMessage("page: " + GET_KIT_USER_LIST_PAGE_MIN);
    }

    @Test
    void testGetKitUserList_SizeIsLessThanMin_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GetKitUserListUseCase.Param(1L, 0, -1));
        assertThat(throwable).hasMessage("size: " + GET_KIT_USER_LIST_SIZE_MIN);
    }

    @Test
    void testGetKitUserList_SizeIsMoreThanMax_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GetKitUserListUseCase.Param(1L, 0, 101));
        assertThat(throwable).hasMessage("size: " + GET_KIT_USER_LIST_SIZE_MAX);
    }
}
