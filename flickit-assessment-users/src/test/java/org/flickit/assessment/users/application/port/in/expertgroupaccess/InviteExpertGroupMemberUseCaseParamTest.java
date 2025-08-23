package org.flickit.assessment.users.application.port.in.expertgroupaccess;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.users.common.ErrorMessageKey.INVITE_EXPERT_GROUP_MEMBER_EXPERT_GROUP_ID_NOT_NULL;
import static org.flickit.assessment.users.common.ErrorMessageKey.INVITE_EXPERT_GROUP_MEMBER_USER_ID_NOT_NULL;
import static org.junit.jupiter.api.Assertions.assertThrows;

class InviteExpertGroupMemberUseCaseParamTest {

    @Test
    void testInviteExpertGroupMemberUseCaseParam_expertGroupIdIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.expertGroupId(null)));
        assertThat(throwable).hasMessage("expertGroupId: " + INVITE_EXPERT_GROUP_MEMBER_EXPERT_GROUP_ID_NOT_NULL);
    }

    @Test
    void testInviteExpertGroupMemberUseCaseParam_userIdIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.userId(null)));
        assertThat(throwable).hasMessage("userId: " + INVITE_EXPERT_GROUP_MEMBER_USER_ID_NOT_NULL);
    }

    @Test
    void testInviteExpertGroupMemberUseCaseParam_currentUserIdIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.currentUserId(null)));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }

    private void createParam(Consumer<InviteExpertGroupMemberUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        paramBuilder.build();
    }

    private InviteExpertGroupMemberUseCase.Param.ParamBuilder paramBuilder() {
        return InviteExpertGroupMemberUseCase.Param.builder()
            .expertGroupId(123L)
            .userId(UUID.randomUUID())
            .currentUserId(UUID.randomUUID());
    }
}
