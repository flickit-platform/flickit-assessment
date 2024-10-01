package org.flickit.assessment.kit.application.port.in.attribute;

import jakarta.validation.ConstraintViolationException;
import org.flickit.assessment.kit.application.port.in.attribute.UpdateAttributesOrderUseCase.AttributeParam;
import org.flickit.assessment.kit.application.port.in.attribute.UpdateAttributesOrderUseCase.AttributeParam.AttributeParamBuilder;
import org.junit.jupiter.api.Test;

import java.util.function.Consumer;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.flickit.assessment.kit.common.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UpdateAttributesOrdersUseCaseAttributeParamTest {

    @Test
    void testUpdateAttributesOrderUseCaseAttributeParam_attributeIdParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.id(null)));
        assertThat(throwable).hasMessage("id: " + UPDATE_ATTRIBUTES_ORDER_ATTRIBUTE_ID_NOT_NULL);
    }

    @Test
    void testUpdateAttributesOrderUseCaseAttributeParam_indexParamViolatesConstraints_ErrorMessage() {
        var throwableNullViolates = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.index(null)));
        assertThat(throwableNullViolates).hasMessage("index: " + UPDATE_ATTRIBUTES_ORDER_INDEX_NOT_NULL);
        var throwableMinViolates = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.index(0)));
        assertThat(throwableMinViolates).hasMessage("index: " + UPDATE_ATTRIBUTES_ORDER_INDEX_MIN);
    }

    private void createParam(Consumer<AttributeParam.AttributeParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        paramBuilder.build();
    }

    private AttributeParamBuilder paramBuilder() {
        return AttributeParam.builder()
            .id(2L)
            .index(3);
    }
}