package org.flickit.assessment.kit.application.port.in.questionnaire;

import jakarta.validation.ConstraintViolationException;
import org.apache.commons.lang3.RandomStringUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.*;

class UpdateQuestionnaireUseCaseParamTest {

    @Test
    void testUpdateQuestionnaireParam_kitIdIsNull_ErrorMessage() {
        long questionnaireId = 1L;
        int index = 1;
        String title = "title";
        String description = "description";
        UUID currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new UpdateQuestionnaireUseCase.Param(null,
                questionnaireId,
                index,
                title,
                description,
                currentUserId));
        assertThat(throwable).hasMessage("kitId: " + UPDATE_QUESTIONNAIRE_KIT_ID_NOT_NULL);
    }

    @Test
    void testUpdateQuestionnaireParam_questionnaireIdIsNull_ErrorMessage() {
        long kitId = 1L;
        int index = 1;
        String title = "title";
        String description = "description";
        UUID currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new UpdateQuestionnaireUseCase.Param(kitId,
                null,
                index,
                title,
                description,
                currentUserId));
        assertThat(throwable).hasMessage("questionnaireId: " + UPDATE_QUESTIONNAIRE_QUESTIONNAIRE_ID_NOT_NULL);
    }

    @Test
    void testUpdateQuestionnaireParam_indexIsNull_ErrorMessage() {
        long kitId = 1L;
        long questionnaireId = 1L;
        String title = "title";
        String description = "description";
        UUID currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new UpdateQuestionnaireUseCase.Param(kitId,
                questionnaireId,
                null,
                title,
                description,
                currentUserId));
        assertThat(throwable).hasMessage("index: " + UPDATE_QUESTIONNAIRE_INDEX_NOT_NULL);
    }

    @Test
    void testUpdateQuestionnaireParam_titleIsBlank_ErrorMessage() {
        long kitId = 1L;
        long questionnaireId = 1L;
        int index = 1;
        String description = "description";
        UUID currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new UpdateQuestionnaireUseCase.Param(kitId,
                questionnaireId,
                index,
                null,
                description,
                currentUserId));
        assertThat(throwable).hasMessage("title: " + UPDATE_QUESTIONNAIRE_TITLE_NOT_NULL);
    }

    @Test
    void testUpdateQuestionnaireParam_titleLengthIsLowerThanSizeMin_ErrorMessage() {
        long kitId = 1L;
        long questionnaireId = 1L;
        int index = 1;
        String title = "ab";
        String description = "description";
        UUID currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new UpdateQuestionnaireUseCase.Param(kitId,
                questionnaireId,
                index,
                title,
                description,
                currentUserId));
        assertThat(throwable).hasMessage("title: " + UPDATE_QUESTIONNAIRE_TITLE_SIZE_MIN);
    }

    @Test
    void testUpdateQuestionnaireParam_titleLengthIsGreaterThanSizeMax_ErrorMessage() {
        long kitId = 1L;
        long questionnaireId = 1L;
        int index = 1;
        String title = RandomStringUtils.randomAlphabetic(101);
        String description = "description";
        UUID currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new UpdateQuestionnaireUseCase.Param(kitId,
                questionnaireId,
                index,
                title,
                description,
                currentUserId));
        assertThat(throwable).hasMessage("title: " + UPDATE_QUESTIONNAIRE_TITLE_SIZE_MAX);
    }

    @Test
    void testUpdateQuestionnaireParam_descriptionIsNull_ErrorMessage() {
        long kitId = 1L;
        long questionnaireId = 1L;
        int index = 1;
        String title = "title";
        UUID currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new UpdateQuestionnaireUseCase.Param(kitId,
                questionnaireId,
                index,
                title,
                null,
                currentUserId));
        assertThat(throwable).hasMessage("description: " + UPDATE_QUESTIONNAIRE_DESCRIPTION_NOT_NULL);
    }

    @Test
    void testUpdateQuestionnaireParam_descriptionIsLowerThanSizeMin_ErrorMessage() {
        long kitId = 1L;
        long questionnaireId = 1L;
        int index = 1;
        String title = "title";
        String description = "ab";
        UUID currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new UpdateQuestionnaireUseCase.Param(kitId,
                questionnaireId,
                index,
                title,
                description,
                currentUserId));
        assertThat(throwable).hasMessage("description: " + UPDATE_QUESTIONNAIRE_DESCRIPTION_SIZE_MIN);
    }

    @Test
    void testUpdateQuestionnaireParam_currentUserIsNull_ErrorMessage() {
        long kitId = 1L;
        long questionnaireId = 1L;
        int index = 1;
        String title = "title";
        String description = "description";
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new UpdateQuestionnaireUseCase.Param(kitId,
                questionnaireId,
                index,
                title,
                description,
                null));
        Assertions.assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }
}
