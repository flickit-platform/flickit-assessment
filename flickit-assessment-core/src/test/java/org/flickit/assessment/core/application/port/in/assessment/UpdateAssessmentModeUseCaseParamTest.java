package org.flickit.assessment.core.application.port.in.assessment;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.*;

class UpdateAssessmentModeUseCaseParamTest {

    @Test
    void testUpdateAssessmentModeUseCaseParam_assessmentIdParamIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
                () -> createParam(b -> b.assessmentId(null)));
        assertThat(throwable).hasMessage("assessmentId: " + UPDATE_ASSESSMENT_MODE_ID_NOT_NULL);
    }

    @Test
    void testUpdateAssessmentModeUseCaseParam_modeParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
                () -> createParam(b -> b.mode(null)));
        assertThat(throwable).hasMessage("mode: " + UPDATE_ASSESSMENT_MODE_MODE_NOT_NULL);

        throwable = assertThrows(ConstraintViolationException.class,
                () -> createParam(b -> b.mode("invalid_mode")));
        assertThat(throwable).hasMessage("mode: " + UPDATE_ASSESSMENT_MODE_MODE_INVALID);
    }

    @Test
    void testUpdateAssessmentModeUseCaseParam_modeParamIsValid_Success() {
        assertDoesNotThrow(() -> createParam(b -> b.mode("ADVANCED")));
    }

    @Test
    void testUpdateAssessmentModeUseCaseParam_currentUserIdParamIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
                () -> createParam(b -> b.currentUserId(null)));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }

    private void createParam(Consumer<UpdateAssessmentModeUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        paramBuilder.build();
    }

    private UpdateAssessmentModeUseCase.Param.ParamBuilder paramBuilder() {
        return UpdateAssessmentModeUseCase.Param.builder()
                .assessmentId(UUID.randomUUID())
                .mode("QUICK")
                .currentUserId(UUID.randomUUID());
    }
}
