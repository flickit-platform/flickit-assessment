package org.flickit.assessment.kit.application.port.in.attribute;

import jakarta.validation.ConstraintViolationException;
import org.flickit.assessment.kit.application.port.in.attribute.UpdateAttributesOrderUseCase.AttributeParam;
import org.flickit.assessment.kit.application.port.in.attribute.UpdateAttributesOrderUseCase.Param;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UpdateAttributesOrderUseCaseParamTest {

    @Test
    void testUpdateAttributesOrderUseCaseParam_kitVersionIdParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.kitVersionId(null)));
        assertThat(throwable).hasMessage("kitVersionId: " + UPDATE_ATTRIBUTES_ORDER_KIT_VERSION_ID_NOT_NULL);
    }

    @Test
    void testUpdateAttributesOrderUseCaseParam_attributesParamViolatesConstraints_ErrorMessage() {
        var throwableNullViolation = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.attributes(null)));
        assertThat(throwableNullViolation).hasMessage("attributes: " + UPDATE_ATTRIBUTES_ORDER_ATTRIBUTES_NOT_NULL);
        var throwableMinViolation = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.attributes(List.of())));
        assertThat(throwableMinViolation).hasMessage("attributes: " + UPDATE_ATTRIBUTES_ORDER_ATTRIBUTES_SIZE_MIN);
    }

    @Test
    void testUpdateAttributesOrderUseCaseParam_currentUserParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.currentUserId(null)));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }

    private void createParam(Consumer<Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        paramBuilder.build();
    }

    private Param.ParamBuilder paramBuilder() {
        return Param.builder()
            .kitVersionId(1L)
            .attributes(List.of(new AttributeParam(2L, 5)))
            .currentUserId(UUID.randomUUID());
    }
}