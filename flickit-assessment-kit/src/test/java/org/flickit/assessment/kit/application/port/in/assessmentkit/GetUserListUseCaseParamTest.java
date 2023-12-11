package org.flickit.assessment.kit.application.port.in.assessmentkit;

import jakarta.validation.ConstraintViolationException;
import org.flickit.assessment.kit.application.port.in.user.GetUserListUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.kit.common.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class GetUserListUseCaseParamTest {

    @Test
    void testGetUserList_kitIdIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GetUserListUseCase.Param(null, 0, 10));
        assertThat(throwable).hasMessage("kitId: " + GET_USER_LIST_KIT_ID_NOT_NULL);
    }

    @Test
    void testGetUserList_PageIsLessThanMin_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GetUserListUseCase.Param(1L, -1, 10));
        assertThat(throwable).hasMessage("page: " + GET_USER_LIST_PAGE_MIN);
    }

    @Test
    void testGetUserList_SizeIsLessThanMin_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GetUserListUseCase.Param(1L, 0, -1));
        assertThat(throwable).hasMessage("size: " + GET_USER_LIST_SIZE_MIN);
    }

    @Test
    void testGetUserList_SizeIsMoreThanMax_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GetUserListUseCase.Param(1L, 0, 101));
        assertThat(throwable).hasMessage("size: " + GET_USER_LIST_SIZE_MAX);
    }
}
