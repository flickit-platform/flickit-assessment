package org.flickit.assessment.kit.application.port.in.questionimpact;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.DELETE_QUESTION_IMPACT_KIT_VERSION_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.DELETE_QUESTION_IMPACT_QUESTION_IMPACT_ID_NOT_NULL;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DeleteQuestionImpactUseCaseParamTest {

    @Test
    void testDeleteQuestionImpactUseCaseParam_questionImpactIdViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.questionImpactId(null)));
        assertThat(throwable).hasMessage("questionImpactId: " + DELETE_QUESTION_IMPACT_QUESTION_IMPACT_ID_NOT_NULL);
    }

    @Test
    void testDeleteQuestionImpactUseCaseParam_kitVersionIdViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.kitVersionId(null)));
        assertThat(throwable).hasMessage("kitVersionId: " + DELETE_QUESTION_IMPACT_KIT_VERSION_ID_NOT_NULL);
    }

    @Test
    void testDeleteQuestionImpactUseCaseParam_CurrentUserIdViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.currentUserId(null)));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }

    private void createParam(Consumer<DeleteQuestionImpactUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        paramBuilder.build();
    }

    private DeleteQuestionImpactUseCase.Param.ParamBuilder paramBuilder() {
        return DeleteQuestionImpactUseCase.Param.builder()
            .questionImpactId(1L)
            .kitVersionId(2L)
            .currentUserId(UUID.randomUUID());
    }
}
