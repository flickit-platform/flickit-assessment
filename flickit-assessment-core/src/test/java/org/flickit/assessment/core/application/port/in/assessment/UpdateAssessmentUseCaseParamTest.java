package org.flickit.assessment.core.application.port.in.assessment;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UpdateAssessmentUseCaseParamTest {

    @Test
    void testUpdateAssessmentParam_IdIsNull_ErrorMessage() {
        String title = "title";
        UUID currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new UpdateAssessmentUseCase.Param(null, title, "shortTitle", currentUserId));
        assertThat(throwable).hasMessage("id: " + UPDATE_ASSESSMENT_ID_NOT_NULL);
    }

    @Test
    void testUpdateAssessmentParam_ShortTitleSizeIsLessThanMin_ErrorMessage() {
        UUID id = UUID.randomUUID();
        UUID currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new UpdateAssessmentUseCase.Param(id, "title", "ab", currentUserId));
        assertThat(throwable).hasMessage("shortTitle: " + UPDATE_ASSESSMENT_SHORT_TITLE_SIZE_MIN);
    }

    @Test
    void testUpdateAssessmentParam_ShortTitleSizeIsGreaterThanMax_ErrorMessage() {
        UUID id = UUID.randomUUID();
        var shortTitle = randomAlphabetic(21);
        UUID currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new UpdateAssessmentUseCase.Param(id, "title", shortTitle, currentUserId));
        assertThat(throwable).hasMessage("shortTitle: " + UPDATE_ASSESSMENT_SHORT_TITLE_SIZE_MAX);
    }

    @Test
    void testUpdateAssessmentParam_TitleIsBlank_ErrorMessage() {
        UUID id = UUID.randomUUID();
        UUID currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new UpdateAssessmentUseCase.Param(id, "    ", "shortTitle", currentUserId));
        assertThat(throwable).hasMessage("title: " + UPDATE_ASSESSMENT_TITLE_NOT_BLANK);
    }

    @Test
    void testUpdateAssessmentParam_TitleSizeIsLessThanMin_ErrorMessage() {
        UUID id = UUID.randomUUID();
        UUID currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new UpdateAssessmentUseCase.Param(id, "ab", "shortTitle", currentUserId));
        assertThat(throwable).hasMessage("title: " + UPDATE_ASSESSMENT_TITLE_SIZE_MIN);
    }

    @Test
    void testUpdateAssessmentParam_TitleSizeIsEqualToMin_ErrorMessage() {
        UUID id = UUID.randomUUID();
        UUID currentUserId = UUID.randomUUID();
        assertDoesNotThrow(
            () -> new UpdateAssessmentUseCase.Param(id, "abc", "shortTitle", currentUserId));
    }

    @Test
    void testUpdateAssessmentParam_TitleSizeIsGreaterThanMax_ErrorMessage() {
        UUID id = UUID.randomUUID();
        var title = randomAlphabetic(101);
        UUID currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new UpdateAssessmentUseCase.Param(id, title, "shortTitle", currentUserId));
        assertThat(throwable).hasMessage("title: " + UPDATE_ASSESSMENT_TITLE_SIZE_MAX);
    }

    @Test
    void testUpdateAssessmentParam_TitleSizeIsEqualToMax_ErrorMessage() {
        UUID id = UUID.randomUUID();
        var title = randomAlphabetic(100);
        UUID currentUserId = UUID.randomUUID();
        assertDoesNotThrow(
            () -> new UpdateAssessmentUseCase.Param(id, title, "shortTitle", currentUserId));
    }

    @Test
    void testUpdateAssessmentParam_currentUserIdIsNull_ErrorMessage() {
        UUID id = UUID.randomUUID();
        String title = "title";
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new UpdateAssessmentUseCase.Param(id, title, "shortTitle", null));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }
}
