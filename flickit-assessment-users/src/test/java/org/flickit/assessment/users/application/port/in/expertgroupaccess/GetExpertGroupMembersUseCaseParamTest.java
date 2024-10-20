package org.flickit.assessment.users.application.port.in.expertgroupaccess;

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
class GetExpertGroupMembersUseCaseParamTest {

    @Test
    void testGetExpertGroupMembers_expertGroupIdIsNull_ErrorMessage() {
        UUID currentUserId = UUID.randomUUID();
        int size = 10;
        int page = 0;
        String status = "ACTIVE";
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GetExpertGroupMembersUseCase.Param(null ,status, currentUserId, size, page));
        assertThat(throwable).hasMessage("id: " + GET_EXPERT_GROUP_MEMBERS_ID_NOT_NULL);
    }

    @Test
    void testGetExpertGroupMembers_currentUserIdIsNull_ErrorMessage() {
        long expertGroupId = 0L;
        int size = 10;
        int page = 0;
        String status = "ACTIVE";

        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GetExpertGroupMembersUseCase.Param(expertGroupId, status, null, size, page));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }

    @Test
    void testGetExpertGroupMembers_sizeMin_ErrorMessage() {
        long expertGroupId = 0L;
        UUID currentUserId = UUID.randomUUID();
        int size = -1;
        int page = 0;
        String status = "ACTIVE";

        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GetExpertGroupMembersUseCase.Param(expertGroupId,status, currentUserId, size, page));
        assertThat(throwable).hasMessage("size: " + GET_EXPERT_GROUP_MEMBERS_SIZE_MIN);
    }

    @Test
    void testGetExpertGroupMembers_sizeMax_ErrorMessage() {
        long expertGroupId = 0L;
        UUID currentUserId = UUID.randomUUID();
        int size = 101;
        int page = 0;
        String status = "ACTIVE";

        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GetExpertGroupMembersUseCase.Param(expertGroupId, status, currentUserId, size, page));
        assertThat(throwable).hasMessage("size: " + GET_EXPERT_GROUP_MEMBERS_SIZE_MAX);
    }

    @Test
    void testGetExpertGroupMembers_pageMin_ErrorMessage() {
        long expertGroupId = 0L;
        UUID currentUserId = UUID.randomUUID();
        int size = 10;
        int page = -1;
        String status = "ACTIVE";

        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GetExpertGroupMembersUseCase.Param(expertGroupId, status,  currentUserId, size, page));
        assertThat(throwable).hasMessage("page: " + GET_EXPERT_GROUP_MEMBERS_PAGE_MIN);
    }

    @Test
    void testGetExpertGroupMembers_statusInvalid_ErrorMessage() {
        long expertGroupId = 0L;
        UUID currentUserId = UUID.randomUUID();
        int size = 10;
        int page = 1;
        String status = "SOMETHING";

        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GetExpertGroupMembersUseCase.Param(expertGroupId, status,  currentUserId, size, page));
        assertThat(throwable).hasMessage("status: " + GET_EXPERT_GROUP_MEMBERS_STATUS_INVALID);
    }

}
