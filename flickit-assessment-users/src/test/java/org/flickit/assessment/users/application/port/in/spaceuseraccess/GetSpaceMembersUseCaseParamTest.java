package org.flickit.assessment.users.application.port.in.spaceuseraccess;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.users.common.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class GetSpaceMembersUseCaseParamTest {

    @Test
    void testGetSpaceMembers_spaceIdIsNull_ErrorMessage() {
        UUID currentUserId = UUID.randomUUID();
        int size = 10;
        int page = 0;
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GetSpaceMembersUseCase.Param(null, currentUserId, size, page));
        assertThat(throwable).hasMessage("id: " + GET_SPACE_MEMBERS_SPACE_ID_NOT_NULL);
    }

    @Test
    void testGetSpaceMembers_currentUserIdIsNull_ErrorMessage() {
        long spaceId = 0L;
        int size = 10;
        int page = 0;
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GetSpaceMembersUseCase.Param(spaceId, null, size, page));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }

    @Test
    void testGetSpaceMembers_sizeMin_ErrorMessage() {
        long spaceId = 0L;
        UUID currentUserId = UUID.randomUUID();
        int size = -1;
        int page = 0;
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GetSpaceMembersUseCase.Param(spaceId, currentUserId, size, page));
        assertThat(throwable).hasMessage("size: " + GET_SPACE_MEMBERS_SIZE_MIN);
    }

    @Test
    void testGetSpaceMembers_sizeMax_ErrorMessage() {
        long spaceId = 0L;
        UUID currentUserId = UUID.randomUUID();
        int size = 101;
        int page = 0;
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GetSpaceMembersUseCase.Param(spaceId, currentUserId, size, page));
        assertThat(throwable).hasMessage("size: " + GET_SPACE_MEMBERS_SIZE_MAX);
    }

    @Test
    void testGetSpaceMembers_pageMin_ErrorMessage() {
        long spaceId = 0L;
        UUID currentUserId = UUID.randomUUID();
        int size = 10;
        int page = -1;
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GetSpaceMembersUseCase.Param(spaceId, currentUserId, size, page));
        assertThat(throwable).hasMessage("page: " + GET_SPACE_MEMBERS_PAGE_MIN);
    }

}
