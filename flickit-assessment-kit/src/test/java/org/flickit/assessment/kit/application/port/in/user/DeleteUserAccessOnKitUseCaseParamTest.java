package org.flickit.assessment.kit.application.port.in.user;

import jakarta.validation.ConstraintViolationException;
import org.flickit.assessment.kit.application.port.in.assessmentkit.DeleteUserAccessOnKitUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.kit.common.ErrorMessageKey.DELETE_USER_ACCESS_KIT_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.DELETE_USER_ACCESS_CURRENT_USER_ID_NOT_NULL;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class DeleteUserAccessOnKitUseCaseParamTest {

    @Test
    void testDeleteUserAccess_KitIdNull_ErrorMessage() {
        UUID userId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new DeleteUserAccessOnKitUseCase.Param(null, "email", userId));
        assertThat(throwable).hasMessage("kitId: " + DELETE_USER_ACCESS_KIT_ID_NOT_NULL);
    }

    @Test
    void testDeleteUserAccess_UserIdNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new DeleteUserAccessOnKitUseCase.Param(1L, "email", null));
        assertThat(throwable).hasMessage("userId: " + DELETE_USER_ACCESS_CURRENT_USER_ID_NOT_NULL);
    }

}
