package org.flickit.assessment.core.application.port.in.assessment;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.ASSIGN_KIT_CUSTOM_ASSESSMENT_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.ASSIGN_KIT_CUSTOM_KIT_CUSTOM_ID_NOT_NULL;
import static org.junit.jupiter.api.Assertions.*;

class AssignKitCustomUseCaseParamTest {

    @Test
    void testAssignKitCustomUseCaseParam_assessmentIdParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.assessmentId(null)));
        assertThat(throwable).hasMessage("assessmentId: " + ASSIGN_KIT_CUSTOM_ASSESSMENT_ID_NOT_NULL);
    }

    @Test
    void testAssignKitCustomUseCaseParam_kitCustomIdParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.kitCustomId(null)));
        assertThat(throwable).hasMessage("kitCustomId: " + ASSIGN_KIT_CUSTOM_KIT_CUSTOM_ID_NOT_NULL);
    }

    @Test
    void testAssignKitCustomUseCaseParam_currentUserIdParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.currentUserId(null)));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }

    private void createParam(Consumer<AssignKitCustomUseCase.Param.ParamBuilder> changer) {
        var param = paramBuilder();
        changer.accept(param);
        param.build();
    }

    private AssignKitCustomUseCase.Param.ParamBuilder paramBuilder() {
        return AssignKitCustomUseCase.Param.builder()
            .assessmentId(UUID.randomUUID())
            .kitCustomId(1L)
            .currentUserId(UUID.randomUUID());
    }
}
