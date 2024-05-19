package org.flickit.assessment.users.application.port.in.spaceinvitee;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.users.common.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GetSpaceInviteesUseCaseParamTest {

    @Test
    void testGetSpaceInvitees_spaceIdIsNull_ErrorMessage() {
        UUID currentUserId = UUID.randomUUID();
        int size = 10;
        int page = 0;
        var throwable = assertThrows(ConstraintViolationException.class,
                () -> new GetSpaceInviteesUseCase.Param(null, currentUserId, size, page));
        assertThat(throwable).hasMessage("id: " + GET_SPACE_INVITEES_SPACE_ID_NOT_NULL);
    }

    @Test
    void testGetSpaceInvitees_currentUserIdIsNull_ErrorMessage() {
        long spaceId = 0L;
        int size = 10;
        int page = 0;
        var throwable = assertThrows(ConstraintViolationException.class,
                () -> new GetSpaceInviteesUseCase.Param(spaceId, null, size, page));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }

    @Test
    void testGetSpaceInvitees_sizeIsLessThanMin_ErrorMessage() {
        long spaceId = 0L;
        UUID currentUserId = UUID.randomUUID();
        int size = -1;
        int page = 0;
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GetSpaceInviteesUseCase.Param(spaceId, currentUserId, size, page));
        assertThat(throwable).hasMessage("size: " + GET_SPACE_INVITEES_SIZE_MIN);
    }

    @Test
    void testGetSpaceInvitees_sizeIsGreaterThanMax_ErrorMessage() {
        long spaceId = 0L;
        UUID currentUserId = UUID.randomUUID();
        int size = 101;
        int page = 0;
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GetSpaceInviteesUseCase.Param(spaceId, currentUserId, size, page));
        assertThat(throwable).hasMessage("size: " + GET_SPACE_INVITEES_SIZE_MAX);
    }

    @Test
    void testGetSpaceInvitees_pageIsLessThanMin_ErrorMessage() {
        long spaceId = 0L;
        UUID currentUserId = UUID.randomUUID();
        int size = 10;
        int page = -1;
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GetSpaceInviteesUseCase.Param(spaceId, currentUserId, size, page));
        assertThat(throwable).hasMessage("page: " + GET_SPACE_INVITEES_PAGE_MIN);
    }

}
