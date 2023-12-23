package org.flickit.assessment.kit.application.port.in.assessmentkit;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.DELETE_KIT_USER_ACCESS_EMAIL_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.DELETE_KIT_USER_ACCESS_KIT_ID_NOT_NULL;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class DeleteKitUserAccessUseCaseParamTest {

    @Test
    void testDeleteUserAccess_KitIdNull_ErrorMessage() {
        UUID currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new DeleteKitUserAccessUseCase.Param(null, "email", currentUserId));
        assertThat(throwable).hasMessage("kitId: " + DELETE_KIT_USER_ACCESS_KIT_ID_NOT_NULL);
    }

    @Test
    void testDeleteUserAccess_EmailIsBlank_ErrorMessage() {
        UUID currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new DeleteKitUserAccessUseCase.Param(1L, "", currentUserId));
        assertThat(throwable).hasMessage("email: " + DELETE_KIT_USER_ACCESS_EMAIL_NOT_NULL);
    }

    @Test
    void testDeleteUserAccess_currentUserIdNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new DeleteKitUserAccessUseCase.Param(1L, "email", null));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }

}
