package org.flickit.assessment.core.application.port.in.assessmentinvite;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_EMAIL_FORMAT_NOT_VALID;
import static org.flickit.assessment.core.common.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class InviteAssessmentUserUseCaseParamTest {

    @Test
    void testInviteAssessmentUserParam_assessmentIdIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.assessmentId(null)));
        assertThat(throwable).hasMessage("assessmentId: " + INVITE_ASSESSMENT_USER_ASSESSMENT_ID_NOT_NULL);
    }

    @Test
    void testInviteAssessmentUserParam_emailParamViolatesConstraint_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.email(null)));
        assertThat(throwable).hasMessage("email: " + INVITE_ASSESSMENT_USER_EMAIL_NOT_NULL);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.email("   ")));
        assertThat(throwable).hasMessage("email: " + INVITE_ASSESSMENT_USER_EMAIL_NOT_NULL);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.email("test.com")));
        assertThat(throwable).hasMessage("email: " + COMMON_EMAIL_FORMAT_NOT_VALID);

        var param1 = createParam(b -> b.email("test@test.com"));
        var param2 = createParam(b -> b.email(" Test@test.com    "));
        assertEquals(param1.getEmail(), param2.getEmail(), "The input email should be stripped, and the case should be ignored.");
    }

    @Test
    void testInviteAssessmentUserParam_roleIdIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.roleId(null)));
        assertThat(throwable).hasMessage("roleId: " + INVITE_ASSESSMENT_USER_ROLE_ID_NOT_NULL);
    }

    @Test
    void testInviteAssessmentUserParam_currentUserIdIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.currentUserId(null)));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }

    private InviteAssessmentUserUseCase.Param createParam(Consumer<InviteAssessmentUserUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private InviteAssessmentUserUseCase.Param.ParamBuilder paramBuilder() {
        return InviteAssessmentUserUseCase.Param.builder()
            .assessmentId(UUID.randomUUID())
            .email("user@email.com")
            .roleId(3)
            .currentUserId(UUID.randomUUID());
    }
}
