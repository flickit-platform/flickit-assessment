package org.flickit.assessment.kit.application.port.in.assessmentkit;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.kit.common.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class DeleteUserAccessOnKitUseCaseParamTest {

    @Test
    void testDeleteUserAccess_KitIdNull_ErrorMessage() {
        UUID currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new DeleteUserAccessOnKitUseCase.Param(null, "email", currentUserId));
        assertThat(throwable).hasMessage("kitId: " + DELETE_USER_ACCESS_KIT_ID_NOT_NULL);
    }

    @Test
    void testDeleteUserAccess_EmailIsBlank_ErrorMessage() {
        UUID currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new DeleteUserAccessOnKitUseCase.Param(1L, "", currentUserId));
        assertThat(throwable).hasMessage("email: " + DELETE_USER_ACCESS_EMAIL_NOT_NULL);
    }

    @Test
    void testDeleteUserAccess_currentUserIdNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new DeleteUserAccessOnKitUseCase.Param(1L, "email", null));
        assertThat(throwable).hasMessage("currentUserId: " + DELETE_USER_ACCESS_CURRENT_USER_ID_NOT_NULL);
    }

}
