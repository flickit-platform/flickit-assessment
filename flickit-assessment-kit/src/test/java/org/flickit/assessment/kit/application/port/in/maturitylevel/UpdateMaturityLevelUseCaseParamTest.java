package org.flickit.assessment.kit.application.port.in.maturitylevel;

import jakarta.validation.ConstraintViolationException;
import org.apache.commons.lang3.RandomStringUtils;
import org.flickit.assessment.common.application.domain.kit.translation.MaturityLevelTranslation;
import org.flickit.assessment.common.exception.ValidationException;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.*;
import static org.flickit.assessment.kit.common.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UpdateMaturityLevelUseCaseParamTest {

    @Test
    void testUpdateMaturityLevelUseCaseParam_IdIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.maturityLevelId(null)));
        assertThat(throwable).hasMessage("maturityLevelId: " + UPDATE_MATURITY_LEVEL_MATURITY_LEVEL_ID_NOT_NULL);
    }

    @Test
    void testUpdateMaturityLevelUseCaseParam_kitVersionIdIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.kitVersionId(null)));
        assertThat(throwable).hasMessage("kitVersionId: " + UPDATE_MATURITY_LEVEL_KIT_VERSION_ID_NOT_NULL);
    }

    @Test
    void testUpdateMaturityLevelUseCaseParam_titleParamViolatesConstraint_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.title(null)));
        assertThat(throwable).hasMessage("title: " + UPDATE_MATURITY_LEVEL_TITLE_NOT_NULL);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.title("       t        ")));
        assertThat(throwable).hasMessage("title: " + UPDATE_MATURITY_LEVEL_TITLE_SIZE_MIN);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.title(RandomStringUtils.random(101))));
        assertThat(throwable).hasMessage("title: " + UPDATE_MATURITY_LEVEL_TITLE_SIZE_MAX);
    }

    @Test
    void testUpdateMaturityLevelUseCaseParam_IndexIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.index(null)));
        assertThat(throwable).hasMessage("index: " + UPDATE_MATURITY_LEVEL_INDEX_NOT_NULL);
    }

    @Test
    void testUpdateMaturityLevelUseCaseParam_DescriptionViolatesConstraint_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.description(null)));
        assertThat(throwable).hasMessage("description: " + UPDATE_MATURITY_LEVEL_DESCRIPTION_NOT_NULL);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.description("            a        ")));
        assertThat(throwable).hasMessage("description: " + UPDATE_MATURITY_LEVEL_DESCRIPTION_SIZE_MIN);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.description(RandomStringUtils.random(501))));
        assertThat(throwable).hasMessage("description: " + UPDATE_MATURITY_LEVEL_DESCRIPTION_SIZE_MAX);
    }

    @Test
    void testUpdateMaturityLevelUseCaseParam_ValueIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.value(null)));
        assertThat(throwable).hasMessage("value: " + UPDATE_MATURITY_LEVEL_VALUE_NOT_NULL);
    }

    @Test
    void testUpdateMaturityLevelUseCaseParam_translationsLanguageViolations_ErrorMessage() {
        var throwable = assertThrows(ValidationException.class,
            () -> createParam(a -> a.translations(Map.of("FR", new MaturityLevelTranslation("title", "desc")))));
        assertEquals(COMMON_KIT_LANGUAGE_NOT_VALID, throwable.getMessageKey());
    }

    @Test
    void testUpdateMaturityLevelUseCaseParam_translationsFieldsViolations_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(a -> a.translations(Map.of("EN", new MaturityLevelTranslation("t", "desc")))));
        assertThat(throwable).hasMessage("translations[EN].title: " + TRANSLATION_MATURITY_LEVEL_TITLE_SIZE_MIN);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(a -> a.translations(Map.of("EN", new MaturityLevelTranslation(RandomStringUtils.randomAlphabetic(101), "desc")))));
        assertThat(throwable).hasMessage("translations[EN].title: " + TRANSLATION_MATURITY_LEVEL_TITLE_SIZE_MAX);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(a -> a.translations(Map.of("EN", new MaturityLevelTranslation("title", "de")))));
        assertThat(throwable).hasMessage("translations[EN].description: " + TRANSLATION_MATURITY_LEVEL_DESCRIPTION_SIZE_MIN);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(a -> a.translations(Map.of("EN", new MaturityLevelTranslation("title", RandomStringUtils.randomAlphabetic(501))))));
        assertThat(throwable).hasMessage("translations[EN].description: " + TRANSLATION_MATURITY_LEVEL_DESCRIPTION_SIZE_MAX);
    }

    @Test
    void testUpdateMaturityLevelUseCaseParam_CurrentUserIdIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.currentUserId(null)));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }

    private void createParam(Consumer<UpdateMaturityLevelUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        paramBuilder.build();
    }

    private UpdateMaturityLevelUseCase.Param.ParamBuilder paramBuilder() {
        return UpdateMaturityLevelUseCase.Param.builder()
            .maturityLevelId(1L)
            .kitVersionId(2L)
            .title("title")
            .index(3)
            .description("team description")
            .value(2)
            .translations(Map.of("EN", new MaturityLevelTranslation("title", "desc")))
            .currentUserId(UUID.randomUUID());
    }
}
