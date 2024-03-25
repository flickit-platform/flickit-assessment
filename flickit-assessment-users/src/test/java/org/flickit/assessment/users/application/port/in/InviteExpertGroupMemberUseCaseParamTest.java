package org.flickit.assessment.users.application.port.in;

import jakarta.validation.ConstraintViolationException;
import org.flickit.assessment.users.application.port.in.expertgroupaccess.InviteExpertGroupMemberUseCase;
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

    private static final Long EXPERT_GROUP_ID = 0L;

    private static final UUID USER_ID = UUID.randomUUID();

    private static final UUID CURRENT_USER_ID = UUID.randomUUID();


    @Test
    void testInviteExpertGroupMember_expertGroupIdIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new InviteExpertGroupMemberUseCase.Param(null, USER_ID, CURRENT_USER_ID));
        assertThat(throwable).hasMessage("expertGroupId: " + INVITE_EXPERT_GROUP_MEMBER_EXPERT_GROUP_ID_NOT_NULL);
    }

    @Test
    void testInviteExpertGroupMember_userIdIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new InviteExpertGroupMemberUseCase.Param(EXPERT_GROUP_ID, null, CURRENT_USER_ID));
        assertThat(throwable).hasMessage("userId: " + INVITE_EXPERT_GROUP_MEMBER_USER_ID_NOT_NULL);
    }

    @Test
    void testInviteExpertGroupMember_currentUserIdIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new InviteExpertGroupMemberUseCase.Param(EXPERT_GROUP_ID, USER_ID, null));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }

}
