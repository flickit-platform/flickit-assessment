package org.flickit.assessment.kit.application.port.in.measure;

import jakarta.validation.ConstraintViolationException;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.*;
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
            () -> createParam(b -> b.title(RandomStringUtils.randomAlphabetic(101))));
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
            () -> createParam(b -> b.description(RandomStringUtils.randomAlphabetic(501))));
        assertThat(throwable).hasMessage("description: " + CREATE_MEASURE_DESCRIPTION_SIZE_MAX);
    }

    @Test
    void testCreateMeasureUseCaseParam_currentUserIdIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.currentUserId(null)));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }

    private void createParam(Consumer<CreateMeasureUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        paramBuilder.build();
    }

    private CreateMeasureUseCase.Param.ParamBuilder paramBuilder() {
        return CreateMeasureUseCase.Param.builder()
            .kitVersionId(1L)
            .index(1)
            .title("title")
            .description("description")
            .currentUserId(UUID.randomUUID());
    }
}
