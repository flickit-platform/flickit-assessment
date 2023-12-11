package org.flickit.assessment.kit.application.port.in.user;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.kit.common.ErrorMessageKey.DELETE_USER_ACCESS_KIT_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.DELETE_USER_ACCESS_USER_ID_NOT_NULL;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class DeleteUserAccessUseCaseParamTest {

    @Test
    void testDeleteUserAccess_KitIdNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new DeleteUserAccessUseCase.Param(null, 1L));
        assertThat(throwable).hasMessage("kitId: " + DELETE_USER_ACCESS_KIT_ID_NOT_NULL);
    }

    @Test
    void testDeleteUserAccess_UserIdNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new DeleteUserAccessUseCase.Param(1L, null));
        assertThat(throwable).hasMessage("userId: " + DELETE_USER_ACCESS_USER_ID_NOT_NULL);
    }

}
