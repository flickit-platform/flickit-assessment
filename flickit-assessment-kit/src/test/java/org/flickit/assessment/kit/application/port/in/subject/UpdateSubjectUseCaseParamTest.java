package org.flickit.assessment.kit.application.port.in.subject;

import jakarta.validation.ConstraintViolationException;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.*;

class UpdateSubjectUseCaseParamTest {

    @Test
    void testUpdateSubjectUseCaseParam_kitVersionIdParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.kitVersionId(null)));
        assertThat(throwable).hasMessage("kitVersionId: " + UPDATE_SUBJECT_KIT_VERSION_ID_NOT_NULL);
    }

    @Test
    void testUpdateSubjectUseCaseParam_subjectIdParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.subjectId(null)));
        assertThat(throwable).hasMessage("subjectId: " + UPDATE_SUBJECT_SUBJECT_ID_NOT_NULL);
    }

    @Test
    void testUpdateSubjectUseCaseParam_indexParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.index(null)));
        assertThat(throwable).hasMessage("index: " + UPDATE_SUBJECT_INDEX_NOT_NULL);
    }

    @Test
    void testUpdateSubjectUseCaseParam_titleParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.title(null)));
        assertThat(throwable).hasMessage("title: " + UPDATE_SUBJECT_TITLE_NOT_NULL);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.title("ab")));
        assertThat(throwable).hasMessage("title: " + UPDATE_SUBJECT_TITLE_SIZE_MIN);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.title(RandomStringUtils.randomAlphabetic(101))));
        assertThat(throwable).hasMessage("title: " + UPDATE_SUBJECT_TITLE_SIZE_MAX);
    }

    @Test
    void testUpdateSubjectUseCaseParam_descriptionParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.description(null)));
        assertThat(throwable).hasMessage("description: " + UPDATE_SUBJECT_DESCRIPTION_NOT_NULL);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.description("ab")));
        assertThat(throwable).hasMessage("description: " + UPDATE_SUBJECT_DESCRIPTION_SIZE_MIN);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.description(RandomStringUtils.randomAlphabetic(501))));
        assertThat(throwable).hasMessage("description: " + UPDATE_SUBJECT_DESCRIPTION_SIZE_MAX);
    }

    @Test
    void testUpdateSubjectUseCaseParam_weightParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.weight(null)));
        assertThat(throwable).hasMessage("weight: " + UPDATE_SUBJECT_WEIGHT_NOT_NULL);
    }

    @Test
    void testUpdateSubjectUseCaseParam_currentUserIdParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.currentUserId(null)));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }

    private void createParam(Consumer<UpdateSubjectUseCase.Param.ParamBuilder> changer) {
        var param = paramBuilder();
        changer.accept(param);
        param.build();
    }

    private UpdateSubjectUseCase.Param.ParamBuilder paramBuilder() {
        return UpdateSubjectUseCase.Param.builder()
            .kitVersionId(1L)
            .subjectId(1L)
            .index(1)
            .title("subject title")
            .description("subject description")
            .weight(1)
            .currentUserId(UUID.randomUUID());
    }
}
