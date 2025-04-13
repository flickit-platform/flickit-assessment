package org.flickit.assessment.kit.application.port.in.subject;

import jakarta.validation.ConstraintViolationException;
import org.apache.commons.lang3.RandomStringUtils;
import org.flickit.assessment.common.application.domain.kit.translation.SubjectTranslation;
import org.flickit.assessment.common.exception.ValidationException;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.*;
import static org.flickit.assessment.kit.common.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.*;

class CreateSubjectUseCaseParamTest {

    @Test
    void testCreateSubjectUseCaseParam_kitParamViolatesConstraint_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.kitVersionId(null)));
        assertThat(throwable).hasMessage("kitVersionId: " + CREATE_SUBJECT_KIT_VERSION_ID_NOT_NULL);
    }

    @Test
    void testCreateSubjectUseCaseParam_indexParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.index(null)));
        assertThat(throwable).hasMessage("index: " + CREATE_SUBJECT_INDEX_NOT_NULL);
    }

    @Test
    void testCreateSubjectUseCaseParam_titleParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.title(null)));
        assertThat(throwable).hasMessage("title: " + CREATE_SUBJECT_TITLE_NOT_NULL);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.title("ti")));
        assertThat(throwable).hasMessage("title: " + CREATE_SUBJECT_TITLE_SIZE_MIN);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.title(RandomStringUtils.randomAlphanumeric(101))));
        assertThat(throwable).hasMessage("title: " + CREATE_SUBJECT_TITLE_SIZE_MAX);
    }

    @Test
    void testCreateSubjectUseCaseParam_weight_SuccessWithDefaultValue() {
        var param = assertDoesNotThrow(() -> createParam(b -> b.weight(null)));
        assertEquals(1, param.getWeight());
    }

    @Test
    void testCreateSubjectUseCaseParam_descriptionParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.description(null)));
        assertThat(throwable).hasMessage("description: " + CREATE_SUBJECT_DESCRIPTION_NOT_NULL);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.description("ab")));
        assertThat(throwable).hasMessage("description: " + CREATE_SUBJECT_DESCRIPTION_SIZE_MIN);

        var description = RandomStringUtils.randomAlphanumeric(501);
        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.description(description)));
        assertThat(throwable).hasMessage("description: " + CREATE_SUBJECT_DESCRIPTION_SIZE_MAX);
    }

    @Test
    void testCreateSubjectUseCaseParam_translationsLanguageViolations_ErrorMessage() {
        var throwable = assertThrows(ValidationException.class,
            () -> createParam(a -> a.translations(Map.of("FR", new SubjectTranslation("title", "desc")))));
        assertEquals(COMMON_KIT_LANGUAGE_NOT_VALID, throwable.getMessageKey());
    }

    @Test
    void testCreateSubjectUseCaseParam_translationsFieldsViolations_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(a -> a.translations(Map.of("EN", new SubjectTranslation("t", "desc")))));
        assertThat(throwable).hasMessage("translations[EN].title: " + TRANSLATION_SUBJECT_TITLE_SIZE_MIN);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(a -> a.translations(Map.of("EN", new SubjectTranslation(RandomStringUtils.randomAlphabetic(101), "desc")))));
        assertThat(throwable).hasMessage("translations[EN].title: " + TRANSLATION_SUBJECT_TITLE_SIZE_MAX);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(a -> a.translations(Map.of("EN", new SubjectTranslation("title", "de")))));
        assertThat(throwable).hasMessage("translations[EN].description: " + TRANSLATION_SUBJECT_DESCRIPTION_SIZE_MIN);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(a -> a.translations(Map.of("EN", new SubjectTranslation("title", RandomStringUtils.randomAlphabetic(501))))));
        assertThat(throwable).hasMessage("translations[EN].description: " + TRANSLATION_SUBJECT_DESCRIPTION_SIZE_MAX);
    }

    @Test
    void testCreateSubjectUseCaseParam_currentUserParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.currentUserId(null)));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }

    private CreateSubjectUseCase.Param createParam(Consumer<CreateSubjectUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private CreateSubjectUseCase.Param.ParamBuilder paramBuilder() {
        return CreateSubjectUseCase.Param.builder()
            .kitVersionId(1L)
            .index(3)
            .title("Team")
            .description("team description")
            .weight(2)
            .translations(Map.of("EN", new SubjectTranslation("titl", "desc")))
            .currentUserId(UUID.randomUUID());
    }
}
