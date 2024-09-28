package org.flickit.assessment.kit.application.port.in.attribute;

import jakarta.validation.ConstraintViolationException;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.*;

class CreateAttributeUseCaseParamTest {

    @Test
    void testCreateAttributeUseCaseParam_kitIdIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.kitId(null)));
        assertThat(throwable).hasMessage("kitId: " + CREATE_ATTRIBUTE_KIT_ID_NOT_NULL);
    }

    @Test
    void testCreateAttributeUseCaseParam_indexIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.index(null)));
        assertThat(throwable).hasMessage("index: " + CREATE_ATTRIBUTE_INDEX_NOT_NULL);
    }

    @Test
    void testCreateAttributeUseCaseParam_titleParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.title(null)));
        assertThat(throwable).hasMessage("title: " + CREATE_ATTRIBUTE_TITLE_NOT_NULL);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.title("ab")));
        assertThat(throwable).hasMessage("title: " + CREATE_ATTRIBUTE_TITLE_MIN_SIZE);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.title(RandomStringUtils.randomAlphabetic(101))));
        assertThat(throwable).hasMessage("title: " + CREATE_ATTRIBUTE_TITLE_MAX_SIZE);
    }

    @Test
    void testCreateAttributeUseCaseParam_descriptionIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.description(null)));
        assertThat(throwable).hasMessage("description: " + CREATE_ATTRIBUTE_DESCRIPTION_NOT_BLANK);
    }

    @Test
    void testCreateAttributeUseCaseParam_weightIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.weight(null)));
        assertThat(throwable).hasMessage("weight: " + CREATE_ATTRIBUTE_WEIGHT_NOT_NULL);
    }

    @Test
    void testCreateAttributeUseCaseParam_subjectIdIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.subjectId(null)));
        assertThat(throwable).hasMessage("subjectId: " + CREATE_ATTRIBUTE_SUBJECT_ID_NOT_NULL);
    }

    @Test
    void testCreateAttributeUseCaseParam_currentUserIdIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.currentUserId(null)));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }

    private CreateAttributeUseCase.Param createParam(Consumer<CreateAttributeUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private CreateAttributeUseCase.Param.ParamBuilder paramBuilder() {
        return CreateAttributeUseCase.Param.builder()
            .kitId(1L)
            .index(1)
            .title("software maintainability")
            .description("desc")
            .weight(2)
            .subjectId(1L)
            .currentUserId(UUID.randomUUID());
    }
}
