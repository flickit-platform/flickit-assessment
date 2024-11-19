package org.flickit.assessment.core.application.port.in.assessment;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.SET_KIT_CUSTOM_TO_ASSESSMENT_ASSESSMENT_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.SET_KIT_CUSTOM_TO_ASSESSMENT_KIT_CUSTOM_ID_NOT_NULL;
import static org.junit.jupiter.api.Assertions.*;

class SetKitCustomToAssessmentUseCaseParamTest {

    @Test
    void testSetKitCustomToAssessmentUseCaseParam_assessmentIdParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.assessmentId(null)));
        assertThat(throwable).hasMessage("assessmentId: " + SET_KIT_CUSTOM_TO_ASSESSMENT_ASSESSMENT_ID_NOT_NULL);
    }

    @Test
    void testSetKitCustomToAssessmentUseCaseParam_kitCustomIdParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.kitCustomId(null)));
        assertThat(throwable).hasMessage("kitCustomId: " + SET_KIT_CUSTOM_TO_ASSESSMENT_KIT_CUSTOM_ID_NOT_NULL);
    }

    @Test
    void testSetKitCustomToAssessmentUseCaseParam_currentUserIdParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.currentUserId(null)));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }

    private void createParam(Consumer<SetKitCustomToAssessmentUseCase.Param.ParamBuilder> changer) {
        var param = paramBuilder();
        changer.accept(param);
        param.build();
    }

    private SetKitCustomToAssessmentUseCase.Param.ParamBuilder paramBuilder() {
        return SetKitCustomToAssessmentUseCase.Param.builder()
            .assessmentId(UUID.randomUUID())
            .kitCustomId(1L)
            .currentUserId(UUID.randomUUID());
    }
}
