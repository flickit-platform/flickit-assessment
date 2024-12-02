package org.flickit.assessment.advice.application.port.in.adviceitem;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.advice.common.ErrorMessageKey.DELETE_ADVICE_ITEM_ASSESSMENT_ID_NOT_NULL;
import static org.flickit.assessment.advice.common.ErrorMessageKey.DELETE_ADVICE_ITEM_ID_NOT_NULL;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.junit.jupiter.api.Assertions.*;

class DeleteAdviceItemUseCaseParamTest {

    @Test
    void testDeleteAdviceItemParam_adviceItemIdParamViolatesConstraint_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.adviceItemId(null)));
        assertThat(throwable).hasMessage("adviceItemId: " + DELETE_ADVICE_ITEM_ID_NOT_NULL);
    }

    @Test
    void testDeleteAdviceItemParam_assessmentIdParamViolatesConstraint_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.assessmentId(null)));
        assertThat(throwable).hasMessage("assessmentId: " + DELETE_ADVICE_ITEM_ASSESSMENT_ID_NOT_NULL);
    }

    @Test
    void testDeleteAdviceItemParam_currentUserIdParamViolatesConstraint_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.currentUserId(null)));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }

    private void createParam(Consumer<DeleteAdviceItemUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        paramBuilder.build();
    }

    private DeleteAdviceItemUseCase.Param.ParamBuilder paramBuilder() {
        return DeleteAdviceItemUseCase.Param.builder()
            .adviceItemId(UUID.randomUUID())
            .assessmentId(UUID.randomUUID())
            .currentUserId(UUID.randomUUID());
    }
}
