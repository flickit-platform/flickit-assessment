package org.flickit.assessment.users.application.port.in.expertgroupaccess;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.users.common.ErrorMessageKey.DELETE_EXPERT_GROUP_MEMBER_EXPERT_GROUP_ID_NOT_NULL;
import static org.flickit.assessment.users.common.ErrorMessageKey.DELETE_EXPERT_GROUP_MEMBER_USER_ID_NOT_NULL;
import static org.junit.jupiter.api.Assertions.*;

class DeleteExpertGroupMemberUseCaseParamTest {

    @Test
    void testDeleteExpertGroupMember_expertGroupIdIsNull_ErrorMessage() {
        UUID userId = UUID.randomUUID();
        UUID currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new DeleteExpertGroupMemberUseCase.Param(null, userId, currentUserId));
        assertThat(throwable).hasMessage("expertGroupId: " + DELETE_EXPERT_GROUP_MEMBER_EXPERT_GROUP_ID_NOT_NULL);
    }

    @Test
    void testDeleteExpertGroupMember_userIdIsNull_ErrorMessage() {
        long expertGroupId = 0L;
        UUID currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new DeleteExpertGroupMemberUseCase.Param(expertGroupId, null, currentUserId));
        assertThat(throwable).hasMessage("userId: " + DELETE_EXPERT_GROUP_MEMBER_USER_ID_NOT_NULL);
    }

    @Test
    void testDeleteExpertGroupMember_currentUserIdIsNull_ErrorMessage() {
        UUID userId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new DeleteExpertGroupMemberUseCase.Param(123L, userId, null));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }

}
