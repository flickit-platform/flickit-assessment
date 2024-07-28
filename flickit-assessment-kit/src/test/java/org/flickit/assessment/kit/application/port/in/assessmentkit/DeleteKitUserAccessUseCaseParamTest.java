package org.flickit.assessment.kit.application.port.in.assessmentkit;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.DELETE_KIT_USER_ACCESS_KIT_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.DELETE_KIT_USER_ACCESS_USER_ID_NOT_NULL;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class DeleteKitUserAccessUseCaseParamTest {

    @Test
    void testDeleteKitUserAccess_KitIdNull_ErrorMessage() {
        UUID currentUserId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new DeleteKitUserAccessUseCase.Param(null, userId, currentUserId));
        assertThat(throwable).hasMessage("kitId: " + DELETE_KIT_USER_ACCESS_KIT_ID_NOT_NULL);
    }

    @Test
    void testDeleteKitUserAccess_UserIdIsNull_ErrorMessage() {
        UUID currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new DeleteKitUserAccessUseCase.Param(1L, null, currentUserId));
        assertThat(throwable).hasMessage("userId: " + DELETE_KIT_USER_ACCESS_USER_ID_NOT_NULL);
    }

    @Test
    void testDeleteKitUserAccess_currentUserIdNull_ErrorMessage() {
        UUID userId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new DeleteKitUserAccessUseCase.Param(1L, userId, null));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }

}
