package org.flickit.assessment.users.application.port.in.spaceuseraccess;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_EMAIL_FORMAT_NOT_VALID;
import static org.flickit.assessment.users.common.ErrorMessageKey.ADD_SPACE_MEMBER_EMAIL_NOT_NULL;
import static org.flickit.assessment.users.common.ErrorMessageKey.ADD_SPACE_MEMBER_SPACE_ID_NOT_NULL;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AddSpaceMemberUseCaseParamTest {

    @Test
    void testAddSpaceMember_spaceIdParamIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.spaceId(null)));
        assertThat(throwable).hasMessage("spaceId: " + ADD_SPACE_MEMBER_SPACE_ID_NOT_NULL);
    }

    @Test
    void testAddSpaceMember_emailParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.email(null)));
        assertThat(throwable).hasMessage("email: " + ADD_SPACE_MEMBER_EMAIL_NOT_NULL);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.email("  ")));
        assertThat(throwable).hasMessage("email: " + ADD_SPACE_MEMBER_EMAIL_NOT_NULL);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.email("test.com")));
        assertThat(throwable).hasMessage("email: " + COMMON_EMAIL_FORMAT_NOT_VALID);
    }

    @ParameterizedTest
    @ValueSource(strings = {"test@test.com", " Test@test.com    "})
    void testAddSpaceMember_Email_SuccessfulStripAndIgnoreCase(String email) {
        assertDoesNotThrow(() -> createParam(b -> b.email(email)));
    }

    @Test
    void testAddSpaceMember_currentUserIdParamIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.currentUserId(null)));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }

    private void createParam(Consumer<AddSpaceMemberUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        paramBuilder.build();
    }

    private AddSpaceMemberUseCase.Param.ParamBuilder paramBuilder() {
        return AddSpaceMemberUseCase.Param.builder()
            .spaceId(0L)
            .email("admin@flickit.org")
            .currentUserId(UUID.randomUUID());
    }
}
