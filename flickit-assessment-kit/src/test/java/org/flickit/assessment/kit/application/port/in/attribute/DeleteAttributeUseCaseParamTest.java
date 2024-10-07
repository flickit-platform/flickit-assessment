package org.flickit.assessment.kit.application.port.in.attribute;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.function.Consumer;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.DELETE_ATTRIBUTE_ATTRIBUTE_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.DELETE_ATTRIBUTE_KIT_VERSION_ID_NOT_NULL;
import static org.junit.jupiter.api.Assertions.*;

class DeleteAttributeUseCaseParamTest {

    @Test
    void testDeleteAttributeUseCaseParam_kitVersionIdParamViolatesConstraints_ErrorMessage() {
        ConstraintViolationException throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.kitVersionId(null)));
        assertThat(throwable).hasMessage("kitVersionId: " + DELETE_ATTRIBUTE_KIT_VERSION_ID_NOT_NULL);
    }

    @Test
    void testDeleteAttributeUseCaseParam_attributeIdParamViolatesConstraints_ErrorMessage() {
        ConstraintViolationException throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.attributeId(null)));
        assertThat(throwable).hasMessage("attributeId: " + DELETE_ATTRIBUTE_ATTRIBUTE_ID_NOT_NULL);
    }

    @Test
    void testDeleteAttributeUseCaseParam_currentUserIdViolatesConstraints_ErrorMessage() {
        ConstraintViolationException throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.currentUserId(null)));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }

    private void createParam(Consumer<DeleteAttributeUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        paramBuilder.build();
    }

    private DeleteAttributeUseCase.Param.ParamBuilder paramBuilder() {
        return DeleteAttributeUseCase.Param.builder()
            .kitVersionId(1L)
            .attributeId(1L)
            .currentUserId(UUID.randomUUID());
    }
}
