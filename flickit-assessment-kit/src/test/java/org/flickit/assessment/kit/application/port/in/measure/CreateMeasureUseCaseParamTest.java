package org.flickit.assessment.kit.application.port.in.measure;

import jakarta.validation.ConstraintViolationException;
import org.flickit.assessment.common.application.domain.kit.translation.MeasureTranslation;
import org.flickit.assessment.common.exception.ValidationException;
import org.flickit.assessment.kit.application.port.in.measure.CreateMeasureUseCase.Param;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.*;
import static org.flickit.assessment.kit.common.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CreateMeasureUseCaseParamTest {

    @Test
    void testCreateMeasureUseCaseParam_kitIdIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.kitVersionId(null)));
        assertThat(throwable).hasMessage("kitVersionId: " + CREATE_MEASURE_KIT_VERSION_ID_NOT_NULL);
    }

    @Test
    void testCreateMeasureUseCaseParam_indexIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.index(null)));
        assertThat(throwable).hasMessage("index: " + CREATE_MEASURE_INDEX_NOT_NULL);
    }

    @Test
    void testCreateMeasureUseCaseParam_titleParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.title(null)));
        assertThat(throwable).hasMessage("title: " + CREATE_MEASURE_TITLE_NOT_NULL);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.title("ab")));
        assertThat(throwable).hasMessage("title: " + CREATE_MEASURE_TITLE_SIZE_MIN);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.title(randomAlphabetic(101))));
        assertThat(throwable).hasMessage("title: " + CREATE_MEASURE_TITLE_SIZE_MAX);
    }

    @Test
    void testCreateMeasureUseCaseParam_descriptionParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.description(null)));
        assertThat(throwable).hasMessage("description: " + CREATE_MEASURE_DESCRIPTION_NOT_NULL);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.description("ab")));
        assertThat(throwable).hasMessage("description: " + CREATE_MEASURE_DESCRIPTION_SIZE_MIN);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.description(randomAlphabetic(501))));
        assertThat(throwable).hasMessage("description: " + CREATE_MEASURE_DESCRIPTION_SIZE_MAX);
    }

    @Test
    void testCreateMeasureUseCaseParam_translationsLanguageViolations_ErrorMessage() {
        var throwable = assertThrows(ValidationException.class,
            () -> createParam(a -> a.translations(Map.of("FR", new MeasureTranslation("title", "desc")))));
        assertEquals(COMMON_KIT_LANGUAGE_NOT_VALID, throwable.getMessageKey());
    }

    @Test
    void testCreateMeasureUseCaseParam_translationsFieldsViolations_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(a -> a.translations(Map.of("EN", new MeasureTranslation("t", "desc")))));
        assertThat(throwable).hasMessage("translations[EN].title: " + TRANSLATION_MEASURE_TITLE_SIZE_MIN);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(a -> a.translations(Map.of("EN", new MeasureTranslation(randomAlphabetic(101), "desc")))));
        assertThat(throwable).hasMessage("translations[EN].title: " + TRANSLATION_MEASURE_TITLE_SIZE_MAX);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(a -> a.translations(Map.of("EN", new MeasureTranslation("title", "de")))));
        assertThat(throwable).hasMessage("translations[EN].description: " + TRANSLATION_MEASURE_DESCRIPTION_SIZE_MIN);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(a -> a.translations(Map.of("EN", new MeasureTranslation("title", randomAlphabetic(501))))));
        assertThat(throwable).hasMessage("translations[EN].description: " + TRANSLATION_MEASURE_DESCRIPTION_SIZE_MAX);
    }

    @Test
    void testCreateMeasureUseCaseParam_currentUserIdIsNull_ErrorMessage() {
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
            .index(1)
            .title("title")
            .description("description")
            .translations(Map.of("EN", new MeasureTranslation("title", "desc")))
            .currentUserId(UUID.randomUUID());
    }
}
