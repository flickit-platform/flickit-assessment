package org.flickit.assessment.users.application.port.in.spaceuseraccess;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_EMAIL_FORMAT_NOT_VALID;
import static org.flickit.assessment.users.common.ErrorMessageKey.INVITE_SPACE_MEMBER_EMAIL_NOT_NULL;
import static org.flickit.assessment.users.common.ErrorMessageKey.INVITE_SPACE_MEMBER_SPACE_ID_NOT_NULL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class InviteSpaceMemberUseCaseParamTest {

    @Test
    void testInviteSpaceMemberUseCaseParam_spaceIdIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.spaceId(null)));
        assertThat(throwable).hasMessage("spaceId: " + INVITE_SPACE_MEMBER_SPACE_ID_NOT_NULL);
    }

    @Test
    void testInviteSpaceMemberUseCaseParam_emailIsParamViolatesConstraint_ErrorMessage() {
        UUID currentUserId = UUID.randomUUID();
        long spaceId = 0L;
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.email(null)));
        assertThat(throwable).hasMessage("email: " + INVITE_SPACE_MEMBER_EMAIL_NOT_NULL);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.email("asta.com")));
        assertThat(throwable).hasMessage("email: " + COMMON_EMAIL_FORMAT_NOT_VALID);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.email("   ")));
        assertThat(throwable).hasMessage("email: " + INVITE_SPACE_MEMBER_EMAIL_NOT_NULL);

        var param1 = new InviteSpaceMemberUseCase.Param(spaceId, "test@test.com", currentUserId);
        var param2 = new InviteSpaceMemberUseCase.Param(spaceId, " Test@test.com    ", currentUserId);
        assertEquals(param1.getEmail(), param2.getEmail(), "The input email should be stripped, and the case should be ignored.");

    }

    @Test
    void testInviteSpaceMemberUseCaseParam_currentUserIdIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.currentUserId(null)));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }

    private void createParam(Consumer<InviteSpaceMemberUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        paramBuilder.build();
    }

    private InviteSpaceMemberUseCase.Param.ParamBuilder paramBuilder() {
        return InviteSpaceMemberUseCase.Param.builder()
            .spaceId(123L)
            .email("user@email.com")
            .currentUserId(UUID.randomUUID());
    }
}
