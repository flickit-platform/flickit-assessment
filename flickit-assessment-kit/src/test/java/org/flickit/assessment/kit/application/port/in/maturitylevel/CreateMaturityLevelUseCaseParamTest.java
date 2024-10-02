package org.flickit.assessment.kit.application.port.in.maturitylevel;

import jakarta.validation.ConstraintViolationException;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CreateMaturityLevelUseCaseParamTest {

    @Test
    void testCreateMaturityLevelUseCaseParam_kitIdIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.kitVersionId(null)));
        assertThat(throwable).hasMessage("kitVersionId: " + CREATE_MATURITY_LEVEL_KIT_VERSION_ID_NOT_NULL);
    }

    @Test
    void testCreateMaturityLevelUseCaseParam_indexIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.index(null)));
        assertThat(throwable).hasMessage("index: " + CREATE_MATURITY_LEVEL_INDEX_NOT_NULL);
    }

    @Test
    void testCreateMaturityLevelUseCaseParam_titleParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.title(null)));
        assertThat(throwable).hasMessage("title: " + CREATE_MATURITY_LEVEL_TITLE_NOT_NULL);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.title("ab")));
        assertThat(throwable).hasMessage("title: " + CREATE_MATURITY_LEVEL_TITLE_SIZE_MIN);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.title(RandomStringUtils.randomAlphabetic(101))));
        assertThat(throwable).hasMessage("title: " + CREATE_MATURITY_LEVEL_TITLE_SIZE_MAX);
    }

    @Test
    void testCreateMaturityLevelUseCaseParam_descriptionParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.description(null)));
        assertThat(throwable).hasMessage("description: " + CREATE_MATURITY_LEVEL_DESCRIPTION_NOT_NULL);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.description("ab")));
        assertThat(throwable).hasMessage("description: " + CREATE_MATURITY_LEVEL_DESCRIPTION_SIZE_MIN);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.description(RandomStringUtils.randomAlphabetic(501))));
        assertThat(throwable).hasMessage("description: " + CREATE_MATURITY_LEVEL_DESCRIPTION_SIZE_MAX);
    }

    @Test
    void testCreateMaturityLevelUseCase_valueIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.value(null)));
        assertThat(throwable).hasMessage("value: " + CREATE_MATURITY_LEVEL_VALUE_NOT_NULL);
    }

    @Test
    void testCreateMaturityLevelUseCase_currentUserIdIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.currentUserId(null)));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }

    private void createParam(Consumer<CreateMaturityLevelUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        paramBuilder.build();
    }

    private CreateMaturityLevelUseCase.Param.ParamBuilder paramBuilder() {
        return CreateMaturityLevelUseCase.Param.builder()
            .kitVersionId(1L)
            .index(1)
            .title("title")
            .description("description")
            .value(1)
            .currentUserId(UUID.randomUUID());
    }
}
