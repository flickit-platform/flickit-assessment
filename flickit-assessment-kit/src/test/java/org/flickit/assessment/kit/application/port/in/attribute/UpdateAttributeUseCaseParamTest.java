package org.flickit.assessment.kit.application.port.in.attribute;

import jakarta.validation.ConstraintViolationException;
import org.apache.commons.lang3.RandomStringUtils;
import org.flickit.assessment.common.application.domain.kit.translate.AttributeTranslation;
import org.flickit.assessment.kit.application.port.in.attribute.UpdateAttributeUseCase.Param;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.*;
import static org.flickit.assessment.kit.common.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UpdateAttributeUseCaseParamTest {

    @Test
    void testUpdateAttributeUseCaseParam_kitVersionIdViolations_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(a -> a.kitVersionId(null)));
        assertThat(throwable).hasMessage("kitVersionId: " + UPDATE_ATTRIBUTE_KIT_VERSION_ID_NOT_NULL);
    }

    @Test
    void testUpdateAttributeUseCaseParam_attributeIdViolations_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(a -> a.attributeId(null)));
        assertThat(throwable).hasMessage("attributeId: " + UPDATE_ATTRIBUTE_ATTRIBUTE_ID_NOT_NULL);
    }

    @Test
    void testUpdateAttributeUseCaseParam_titleViolations_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(a -> a.title("      ")));
        assertThat(throwable).hasMessage("title: " + UPDATE_ATTRIBUTE_TITLE_NOT_BLANK);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(a -> a.title("s")));
        assertThat(throwable).hasMessage("title: " + UPDATE_ATTRIBUTE_TITLE_SIZE_MIN);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(a -> a.title(RandomStringUtils.randomAlphabetic(101))));
        assertThat(throwable).hasMessage("title: " + UPDATE_ATTRIBUTE_TITLE_SIZE_MAX);
    }

    @Test
    void testUpdateAttributeUseCaseParam_descriptionViolations_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(a -> a.description("      ")));
        assertThat(throwable).hasMessage("description: " + UPDATE_ATTRIBUTE_DESCRIPTION_NOT_BLANK);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(a -> a.description("a")));
        assertThat(throwable).hasMessage("description: " + UPDATE_ATTRIBUTE_DESCRIPTION_SIZE_MIN);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(a -> a.description(RandomStringUtils.randomAlphabetic(501))));
        assertThat(throwable).hasMessage("description: " + UPDATE_ATTRIBUTE_DESCRIPTION_SIZE_MAX);
    }

    @Test
    void testUpdateAttributeUseCaseParam_subjectIdViolations_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(a -> a.subjectId(null)));
        assertThat(throwable).hasMessage("subjectId: " + UPDATE_ATTRIBUTE_SUBJECT_ID_NOT_NULL);
    }

    @Test
    void testUpdateAttributeUseCaseParam_indexViolations_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(a -> a.index(null)));
        assertThat(throwable).hasMessage("index: " + UPDATE_ATTRIBUTE_INDEX_NOT_NULL);
    }

    @Test
    void testUpdateAttributeUseCaseParam_weightViolations_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(a -> a.weight(null)));
        assertThat(throwable).hasMessage("weight: " + UPDATE_ATTRIBUTE_WEIGHT_NOT_NULL);
    }

    @Test
    void testUpdateAttributeUseCaseParam_translationLanguageViolations_ErrorMessage() {
        var throwable = assertThrows(org.flickit.assessment.common.exception.ValidationException.class,
            () -> createParam(a -> a.translations(Map.of("FR", new AttributeTranslation("title", "desc")))));
        assertEquals(COMMON_KIT_LANGUAGE_NOT_VALID, throwable.getMessageKey());
    }

    @Test
    void testUpdateAttributeUseCaseParam_translationFieldsViolations_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(a -> a.translations(Map.of("EN", new AttributeTranslation("t", "desc")))));
        assertThat(throwable).hasMessage("translations[EN].title: " + TRANSLATE_ATTRIBUTE_TITLE_SIZE_MIN);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(a -> a.translations(Map.of("EN", new AttributeTranslation(RandomStringUtils.randomAlphabetic(101), "desc")))));
        assertThat(throwable).hasMessage("translations[EN].title: " + TRANSLATE_ATTRIBUTE_TITLE_SIZE_MAX);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(a -> a.translations(Map.of("EN", new AttributeTranslation("title", "de")))));
        assertThat(throwable).hasMessage("translations[EN].description: " + TRANSLATE_ATTRIBUTE_DESCRIPTION_SIZE_MIN);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(a -> a.translations(Map.of("EN", new AttributeTranslation("title", RandomStringUtils.randomAlphabetic(501))))));
        assertThat(throwable).hasMessage("translations[EN].description: " + TRANSLATE_ATTRIBUTE_DESCRIPTION_SIZE_MAX);
    }

    @Test
    void testUpdateAttributeUseCaseParam_currentUserIdViolations_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(a -> a.currentUserId(null)));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }

    private void createParam(Consumer<Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        paramBuilder.build();
    }

    private Param.ParamBuilder paramBuilder() {
        return Param.builder()
            .kitVersionId(16L)
            .attributeId(25L)
            .title("title")
            .description("description")
            .subjectId(18L)
            .index(2)
            .weight(1)
            .translations(Map.of("EN", new AttributeTranslation("titl", "desc")))
            .currentUserId(UUID.randomUUID());
    }
}
