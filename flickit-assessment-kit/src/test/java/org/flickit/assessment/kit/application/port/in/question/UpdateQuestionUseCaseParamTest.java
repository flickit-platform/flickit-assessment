package org.flickit.assessment.kit.application.port.in.question;

import jakarta.validation.ConstraintViolationException;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.*;

class UpdateQuestionUseCaseParamTest {

    @Test
    void testUpdateQuestionParam_kitIdIsNull_ErrorMessage() {
        long questionId = 1L;
        int index = 1;
        String title = "title";
        String hint = "hint";
        boolean mayNotBeApplicable = false;
        boolean advisable = false;
        UUID currentUserId = UUID.randomUUID();

        ConstraintViolationException throwable =
                assertThrows(ConstraintViolationException.class, () -> new UpdateQuestionUseCase.Param(null,
                        questionId,
                        index,
                        title,
                        hint,
                        mayNotBeApplicable,
                        advisable,
                        currentUserId));

        assertThat(throwable).hasMessage("kitId: " + UPDATE_QUESTION_KIT_ID_NOT_NULL);
    }

    @Test
    void testUpdateQuestionParam_questionIdIsNull_ErrorMessage() {
        long kitId = 1L;
        int index = 1;
        String title = "title";
        String hint = "hint";
        boolean mayNotBeApplicable = false;
        boolean advisable = false;
        UUID currentUserId = UUID.randomUUID();

        ConstraintViolationException throwable =
                assertThrows(ConstraintViolationException.class, () -> new UpdateQuestionUseCase.Param(kitId,
                        null,
                        index,
                        title,
                        hint,
                        mayNotBeApplicable,
                        advisable,
                        currentUserId));

        assertThat(throwable).hasMessage("questionId: " + UPDATE_QUESTION_QUESTION_ID_NOT_NULL);
    }

    @Test
    void testUpdateQuestionParam_indexIsNull_ErrorMessage() {
        long kitId = 1L;
        long questionId = 1L;
        String title = "title";
        String hint = "hint";
        boolean mayNotBeApplicable = false;
        boolean advisable = false;
        UUID currentUserId = UUID.randomUUID();

        ConstraintViolationException throwable =
                assertThrows(ConstraintViolationException.class, () -> new UpdateQuestionUseCase.Param(kitId,
                        questionId,
                        null,
                        title,
                        hint,
                        mayNotBeApplicable,
                        advisable,
                        currentUserId));

        assertThat(throwable).hasMessage("index: " + UPDATE_QUESTION_INDEX_NOT_NULL);
    }

    @Test
    void testUpdateQuestionParam_titleIsNull_ErrorMessage() {
        long kitId = 1L;
        long questionId = 1L;
        int index = 1;
        String hint = "hint";
        boolean mayNotBeApplicable = false;
        boolean advisable = false;
        UUID currentUserId = UUID.randomUUID();

        ConstraintViolationException throwable =
                assertThrows(ConstraintViolationException.class, () -> new UpdateQuestionUseCase.Param(kitId,
                        questionId,
                        index,
                        null,
                        hint,
                        mayNotBeApplicable,
                        advisable,
                        currentUserId));

        assertThat(throwable).hasMessage("title: " + UPDATE_QUESTION_TITLE_NOT_NULL);
    }

    @Test
    void testUpdateQuestionParam_titleLengthIsLessThanSizeMin_ErrorMessage() {
        long kitId = 1L;
        long questionId = 1L;
        int index = 1;
        String title = "ab";
        String hint = "hint";
        boolean mayNotBeApplicable = false;
        boolean advisable = false;
        UUID currentUserId = UUID.randomUUID();

        ConstraintViolationException throwable =
            assertThrows(ConstraintViolationException.class, () -> new UpdateQuestionUseCase.Param(kitId,
                questionId,
                index,
                title,
                hint,
                mayNotBeApplicable,
                advisable,
                currentUserId));

        assertThat(throwable).hasMessage("title: " + UPDATE_QUESTION_TITLE_SIZE_MIN);
    }

    @Test
    void testUpdateQuestionParam_titleLengthIsGreaterThanSizeMax_ErrorMessage() {
        long kitId = 1L;
        long questionId = 1L;
        int index = 1;
        String title = RandomStringUtils.randomAlphabetic(101);
        String hint = "hint";
        boolean mayNotBeApplicable = false;
        boolean advisable = false;
        UUID currentUserId = UUID.randomUUID();

        ConstraintViolationException throwable =
            assertThrows(ConstraintViolationException.class, () -> new UpdateQuestionUseCase.Param(kitId,
                questionId,
                index,
                title,
                hint,
                mayNotBeApplicable,
                advisable,
                currentUserId));

        assertThat(throwable).hasMessage("title: " + UPDATE_QUESTION_TITLE_SIZE_MAX);
    }

    @Test
    void testUpdateQuestionParam_hintLengthIsLessThanSizeMin_ErrorMessage() {
        long kitId = 1L;
        long questionId = 1L;
        int index = 1;
        String title = "title";
        String hint = "ab";
        boolean mayNotBeApplicable = false;
        boolean advisable = false;
        UUID currentUserId = UUID.randomUUID();

        ConstraintViolationException throwable =
            assertThrows(ConstraintViolationException.class, () -> new UpdateQuestionUseCase.Param(kitId,
                questionId,
                index,
                title,
                hint,
                mayNotBeApplicable,
                advisable,
                currentUserId));

        assertThat(throwable).hasMessage("hint: " + UPDATE_QUESTION_HINT_SIZE_MIN);
    }

    @Test
    void testUpdateQuestionParam_mayNotBeApplicableIsNull_ErrorMessage() {
        long kitId = 1L;
        long questionId = 1L;
        int index = 1;
        String title = "title";
        String hint = "hint";
        boolean advisable = false;
        UUID currentUserId = UUID.randomUUID();

        ConstraintViolationException throwable =
            assertThrows(ConstraintViolationException.class, () -> new UpdateQuestionUseCase.Param(kitId,
                questionId,
                index,
                title,
                hint,
                null,
                advisable,
                currentUserId));

        assertThat(throwable).hasMessage("mayNotBeApplicable: " + UPDATE_QUESTION_MAY_NOT_BE_APPLICABLE_NOT_NULL);
    }

    @Test
    void testUpdateQuestionParam_advisableIsNull_ErrorMessage() {
        long kitId = 1L;
        long questionId = 1L;
        int index = 1;
        String title = "title";
        String hint = "hint";
        boolean mayNotBeApplicable = false;
        UUID currentUserId = UUID.randomUUID();

        ConstraintViolationException throwable =
            assertThrows(ConstraintViolationException.class, () -> new UpdateQuestionUseCase.Param(kitId,
                questionId,
                index,
                title,
                hint,
                mayNotBeApplicable,
                null,
                currentUserId));

        assertThat(throwable).hasMessage("advisable: " + UPDATE_QUESTION_ADVISABLE_NOT_NULL);
    }

    @Test
    void testUpdateQuestionParam_currentUserIdIsNull_ErrorMessage() {
        long kitId = 1L;
        long questionId = 1L;
        int index = 1;
        String title = "title";
        String hint = "hint";
        boolean mayNotBeApplicable = false;
        boolean advisable = false;

        ConstraintViolationException throwable =
            assertThrows(ConstraintViolationException.class, () -> new UpdateQuestionUseCase.Param(kitId,
                questionId,
                index,
                title,
                hint,
                mayNotBeApplicable,
                advisable,
                null));

        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }
}
