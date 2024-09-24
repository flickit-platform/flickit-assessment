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

class CreateSubjectUseCaseParamTest {

    @Test
    void testCreateSubjectUseCaseParam_kitIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.kitId(null)));
        assertThat(throwable).hasMessage("kitId: " + CREATE_SUBJECT_KIT_ID_NOT_NULL);
    }

    @Test
    void testCreateSubjectUseCaseParam_indexIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.index(null)));
        assertThat(throwable).hasMessage("index: " + CREATE_SUBJECT_INDEX_NOT_NULL);
    }

    @Test
    void testCreateSubjectUseCaseParam_titleIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.title(null)));
        assertThat(throwable).hasMessage("title: " + CREATE_SUBJECT_TITLE_NOT_NULL);
    }

    @Test
    void testCreateSubjectUseCaseParam_titleLengthIsLessThanMin_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.title("ti")));
        assertThat(throwable).hasMessage("title: " + CREATE_SUBJECT_TITLE_SIZE_MIN);
    }

    @Test
    void testCreateSubjectUseCaseParam_titleLengthIsGreaterThanMax_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.title(RandomStringUtils.randomAlphanumeric(101))));
        assertThat(throwable).hasMessage("title: " + CREATE_SUBJECT_TITLE_SIZE_MAX);
    }

    @Test
    void testCreateSubjectUseCaseParam_descriptionIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.description(null)));
        assertThat(throwable).hasMessage("description: " + CREATE_SUBJECT_DESCRIPTION_NOT_NULL);
    }

    @Test
    void testCreateSubjectUseCaseParam_descriptionSizeIsLowerThanMin_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.description("ab")));
        assertThat(throwable).hasMessage("description: " + CREATE_SUBJECT_DESCRIPTION_SIZE_MIN);
    }

    @Test
    void testCreateSubjectUseCaseParam_descriptionSizeIsGreaterThanMax_ErrorMessage() {
        String description = RandomStringUtils.randomAlphanumeric(501);
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.description(description)));
        assertThat(throwable).hasMessage("description: " + CREATE_SUBJECT_DESCRIPTION_SIZE_MAX);
    }

    @Test
    void testCreateSubjectUseCaseParam_currentUserIdIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.currentUserId(null)));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }

    private void createParam(Consumer<CreateSubjectUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        paramBuilder.build();
    }

    private CreateSubjectUseCase.Param.ParamBuilder paramBuilder() {
        return CreateSubjectUseCase.Param.builder()
            .kitId(1L)
            .index(3)
            .title("Team")
            .description("team description")
            .weight(2)
            .currentUserId(UUID.randomUUID());
    }
}
