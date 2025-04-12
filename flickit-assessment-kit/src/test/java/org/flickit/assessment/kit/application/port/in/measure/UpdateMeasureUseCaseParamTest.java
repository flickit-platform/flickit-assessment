package org.flickit.assessment.kit.application.port.in.measure;

import jakarta.validation.ConstraintViolationException;
import org.apache.commons.lang3.RandomStringUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.function.Consumer;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.*;

class UpdateMeasureUseCaseParamTest {

    @Test
    void testUpdateMeasureUseCaseParam_kitVersionIdParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.kitVersionId(null)));
        assertThat(throwable).hasMessage("kitVersionId: " + UPDATE_MEASURE_KIT_VERSION_ID_NOT_NULL);
    }

    @Test
    void testUpdateMeasureUseCaseParam_measureIdParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.measureId(null)));
        assertThat(throwable).hasMessage("measureId: " + UPDATE_MEASURE_MEASURE_ID_NOT_NULL);
    }

    @Test
    void testUpdateMeasureUseCaseParam_indexParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.index(null)));
        assertThat(throwable).hasMessage("index: " + UPDATE_MEASURE_INDEX_NOT_NULL);
    }

    @Test
    void testUpdateMeasureUseCaseParam_titleParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.title(null)));
        assertThat(throwable).hasMessage("title: " + UPDATE_MEASURE_TITLE_NOT_NULL);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.title("ab")));
        assertThat(throwable).hasMessage("title: " + UPDATE_MEASURE_TITLE_SIZE_MIN);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.title(RandomStringUtils.randomAlphabetic(101))));
        assertThat(throwable).hasMessage("title: " + UPDATE_MEASURE_TITLE_SIZE_MAX);
    }

    @Test
    void testUpdateMeasureUseCaseParam_descriptionParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.description(null)));
        assertThat(throwable).hasMessage("description: " + UPDATE_MEASURE_DESCRIPTION_NOT_NULL);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.description("ab")));
        assertThat(throwable).hasMessage("description: " + UPDATE_MEASURE_DESCRIPTION_SIZE_MIN);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.description(RandomStringUtils.randomAlphabetic(501))));
        assertThat(throwable).hasMessage("description: " + UPDATE_MEASURE_DESCRIPTION_SIZE_MAX);
    }

    @Test
    void testUpdateMeasureParam_currentUserIdParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.currentUserId(null)));
        Assertions.assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }

    private void createParam(Consumer<UpdateMeasureUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        paramBuilder.build();
    }

    private UpdateMeasureUseCase.Param.ParamBuilder paramBuilder() {
        return UpdateMeasureUseCase.Param.builder()
            .kitVersionId(1L)
            .measureId(1L)
            .title("abc")
            .index(1)
            .description("description")
            .currentUserId(UUID.randomUUID());
    }
}
