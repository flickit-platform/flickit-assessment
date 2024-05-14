package org.flickit.assessment.users.application.port.in.spaceuseraccess;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.users.common.ErrorMessageKey.UPDATE_SPACE_LAST_SEEN_SPACE_ID_NOT_NULL;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UpdateSpaceLastSeenUseCaseParamTest {

    @Test
    void tesUpdateSpaceLastSeen_idIsNull_ErrorMessage() {
        UUID currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new UpdateSpaceLastSeenUseCase.Param(null, currentUserId));
        assertThat(throwable).hasMessage("spaceId: " + UPDATE_SPACE_LAST_SEEN_SPACE_ID_NOT_NULL);
    }

    @Test
    void testUpdateSpaceLastSeen_currentUserIdIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new UpdateSpaceLastSeenUseCase.Param(123L, null));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }
}
