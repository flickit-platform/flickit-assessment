package org.flickit.assessment.kit.application.port.in.attribute;

import jakarta.validation.ConstraintViolationException;
import org.flickit.assessment.common.application.domain.kit.translation.AttributeTranslation;
import org.flickit.assessment.common.exception.ValidationException;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.*;
import static org.flickit.assessment.kit.common.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.*;

class CreateAttributeUseCaseParamTest {

    @Test
    void testCreateAttributeUseCaseParam_kitIdIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.kitVersionId(null)));
        assertThat(throwable).hasMessage("kitVersionId: " + CREATE_ATTRIBUTE_KIT_VERSION_ID_NOT_NULL);
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
            () -> createParam(b -> b.title(randomAlphabetic(101))));
        assertThat(throwable).hasMessage("title: " + CREATE_ATTRIBUTE_TITLE_MAX_SIZE);
    }

    @Test
    void testCreateAttributeUseCaseParam_descriptionParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.description(null)));
        assertThat(throwable).hasMessage("description: " + CREATE_ATTRIBUTE_DESCRIPTION_NOT_NULL);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.description("ab")));
        assertThat(throwable).hasMessage("description: " + CREATE_ATTRIBUTE_DESCRIPTION_SIZE_MIN);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.description(randomAlphabetic(501))));
        assertThat(throwable).hasMessage("description: " + CREATE_ATTRIBUTE_DESCRIPTION_SIZE_MAX);
    }

    @Test
    void testCreateAttributeUseCaseParam_weight_SuccessWithDefaultValue() {
        var param = assertDoesNotThrow(() -> createParam(b -> b.weight(null)));
        assertEquals(1, param.getWeight());
    }

    @Test
    void testCreateAttributeUseCaseParam_subjectIdIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.subjectId(null)));
        assertThat(throwable).hasMessage("subjectId: " + CREATE_ATTRIBUTE_SUBJECT_ID_NOT_NULL);
    }

    @Test
    void testCreateAttributeUseCaseParam_translationsLanguageViolations_ErrorMessage() {
        var throwable = assertThrows(ValidationException.class,
            () -> createParam(a -> a.translations(Map.of("FR", new AttributeTranslation("title", "desc")))));
        assertEquals(COMMON_KIT_LANGUAGE_NOT_VALID, throwable.getMessageKey());
    }

    @Test
    void testCreateAttributeUseCaseParam_translationsFieldsViolations_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(a -> a.translations(Map.of("EN", new AttributeTranslation("t", "desc")))));
        assertThat(throwable).hasMessage("translations[EN].title: " + TRANSLATION_ATTRIBUTE_TITLE_SIZE_MIN);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(a -> a.translations(Map.of("EN", new AttributeTranslation(randomAlphabetic(101), "desc")))));
        assertThat(throwable).hasMessage("translations[EN].title: " + TRANSLATION_ATTRIBUTE_TITLE_SIZE_MAX);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(a -> a.translations(Map.of("EN", new AttributeTranslation("title", "de")))));
        assertThat(throwable).hasMessage("translations[EN].description: " + TRANSLATION_ATTRIBUTE_DESCRIPTION_SIZE_MIN);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(a -> a.translations(Map.of("EN", new AttributeTranslation("title", randomAlphabetic(501))))));
        assertThat(throwable).hasMessage("translations[EN].description: " + TRANSLATION_ATTRIBUTE_DESCRIPTION_SIZE_MAX);
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
            .kitVersionId(1L)
            .index(1)
            .title("software maintainability")
            .description("desc")
            .weight(2)
            .subjectId(1L)
            .translations(Map.of("EN", new AttributeTranslation("titl", "desc")))
            .currentUserId(UUID.randomUUID());
    }
}
