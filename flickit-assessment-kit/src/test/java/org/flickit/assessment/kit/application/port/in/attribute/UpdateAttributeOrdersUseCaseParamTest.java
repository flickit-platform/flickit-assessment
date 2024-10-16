package org.flickit.assessment.kit.application.port.in.attribute;

import jakarta.validation.ConstraintViolationException;
import org.flickit.assessment.kit.application.port.in.attribute.UpdateAttributeOrdersUseCase.AttributeParam;
import org.flickit.assessment.kit.application.port.in.attribute.UpdateAttributeOrdersUseCase.Param;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UpdateAttributeOrdersUseCaseParamTest {

    @Test
    void testUpdateAttributeOrdersUseCaseParam_kitVersionIdParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.kitVersionId(null)));
        assertThat(throwable).hasMessage("kitVersionId: " + UPDATE_ATTRIBUTE_ORDERS_KIT_VERSION_ID_NOT_NULL);
    }

    @Test
    void testUpdateAttributeOrdersUseCaseParam_attributesParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.attributes(null)));
        assertThat(throwable).hasMessage("attributes: " + UPDATE_ATTRIBUTE_ORDERS_ATTRIBUTES_NOT_NULL);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.attributes(List.of(new AttributeParam(2L, 5)))));
        assertThat(throwable).hasMessage("attributes: " + UPDATE_ATTRIBUTE_ORDERS_ATTRIBUTES_SIZE_MIN);
    }

    @Test
    void testUpdateAttributeOrdersUseCaseParam_currentUserParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.currentUserId(null)));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }

    @Test
    void testUpdateAttributeOrdersUseCaseAttributeParam_attributeIdParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createAttributeParam(b -> b.id(null)));
        assertThat(throwable).hasMessage("id: " + UPDATE_ATTRIBUTE_ORDERS_ATTRIBUTE_ID_NOT_NULL);
    }

    @Test
    void testUpdateAttributeOrdersUseCaseAttributeParam_indexParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createAttributeParam(b -> b.index(null)));
        assertThat(throwable).hasMessage("index: " + UPDATE_ATTRIBUTE_ORDERS_INDEX_NOT_NULL);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> createAttributeParam(b -> b.index(0)));
        assertThat(throwable).hasMessage("index: " + UPDATE_ATTRIBUTE_ORDERS_INDEX_MIN);
    }

    private void createParam(Consumer<Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        paramBuilder.build();
    }

    private Param.ParamBuilder paramBuilder() {
        return Param.builder()
            .kitVersionId(1L)
            .attributes(List.of(new AttributeParam(2L, 5), new AttributeParam(3L, 6)))
            .currentUserId(UUID.randomUUID());
    }

    private void createAttributeParam(Consumer<AttributeParam.AttributeParamBuilder> changer) {
        var paramBuilder = attributeParamBuilder();
        changer.accept(paramBuilder);
        paramBuilder.build();
    }

    private AttributeParam.AttributeParamBuilder attributeParamBuilder() {
        return AttributeParam.builder()
            .id(2L)
            .index(3);
    }
}
