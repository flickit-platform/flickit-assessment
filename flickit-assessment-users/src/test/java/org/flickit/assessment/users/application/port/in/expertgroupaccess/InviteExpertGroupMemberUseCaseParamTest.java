package org.flickit.assessment.users.application.port.in.expertgroupaccess;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.users.common.ErrorMessageKey.INVITE_EXPERT_GROUP_MEMBER_EXPERT_GROUP_ID_NOT_NULL;
import static org.flickit.assessment.users.common.ErrorMessageKey.INVITE_EXPERT_GROUP_MEMBER_USER_ID_NOT_NULL;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class InviteExpertGroupMemberUseCaseParamTest {

    @Test
    void testInviteExpertGroupMember_expertGroupIdIsNull_ErrorMessage() {
        UUID userId = UUID.randomUUID();
        UUID currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new InviteExpertGroupMemberUseCase.Param(null, userId, currentUserId));
        assertThat(throwable).hasMessage("expertGroupId: " + INVITE_EXPERT_GROUP_MEMBER_EXPERT_GROUP_ID_NOT_NULL);
    }

    @Test
    void testInviteExpertGroupMember_userIdIsNull_ErrorMessage() {
        UUID currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new InviteExpertGroupMemberUseCase.Param(123L, null, currentUserId));
        assertThat(throwable).hasMessage("userId: " + INVITE_EXPERT_GROUP_MEMBER_USER_ID_NOT_NULL);
    }

    @Test
    void testInviteExpertGroupMember_currentUserIdIsNull_ErrorMessage() {
        UUID userId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new InviteExpertGroupMemberUseCase.Param(123L, userId, null));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }

}
