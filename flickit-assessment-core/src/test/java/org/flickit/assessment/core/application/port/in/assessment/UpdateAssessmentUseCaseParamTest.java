package org.flickit.assessment.core.application.port.in.assessment;

import jakarta.validation.ConstraintViolationException;
import org.flickit.assessment.core.application.domain.AssessmentColor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.core.common.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class UpdateAssessmentUseCaseParamTest {

    @Test
    void testUpdateAssessmentParam_IdIsNull_ErrorMessage() {
        String title = "title";
        int colorId = AssessmentColor.BLUE.getId();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new UpdateAssessmentUseCase.Param(null, title, colorId));
        assertThat(throwable).hasMessage("id: " + UPDATE_ASSESSMENT_ID_NOT_NULL);
    }

    @Test
    void testUpdateAssessmentParam_TitleIsBlank_ErrorMessage() {
        UUID id = UUID.randomUUID();
        int colorId = AssessmentColor.BLUE.getId();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new UpdateAssessmentUseCase.Param(id, "    ", colorId));
        assertThat(throwable).hasMessage("title: " + UPDATE_ASSESSMENT_TITLE_NOT_BLANK);
    }

    @Test
    void testUpdateAssessmentParam_TitleSizeIsLessThanMin_ErrorMessage() {
        UUID id = UUID.randomUUID();
        int colorId = AssessmentColor.BLUE.getId();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new UpdateAssessmentUseCase.Param(id, "ab", colorId));
        assertThat(throwable).hasMessage("title: " + UPDATE_ASSESSMENT_TITLE_SIZE_MIN);
    }

    @Test
    void testUpdateAssessmentParam_TitleSizeIsEqualToMin_ErrorMessage() {
        UUID id = UUID.randomUUID();
        int colorId = AssessmentColor.BLUE.getId();
        assertDoesNotThrow(
            () -> new UpdateAssessmentUseCase.Param(id, "abc", colorId));
    }

    @Test
    void testUpdateAssessmentParam_TitleSizeIsGreaterThanMax_ErrorMessage() {
        UUID id = UUID.randomUUID();
        var title = randomAlphabetic(101);
        int colorId = AssessmentColor.BLUE.getId();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new UpdateAssessmentUseCase.Param(id, title, colorId));
        assertThat(throwable).hasMessage("title: " + UPDATE_ASSESSMENT_TITLE_SIZE_MAX);
    }

    @Test
    void testUpdateAssessmentParam_TitleSizeIsEqualToMax_ErrorMessage() {
        UUID id = UUID.randomUUID();
        var title = randomAlphabetic(100);
        int colorId = AssessmentColor.BLUE.getId();
        assertDoesNotThrow(
            () -> new UpdateAssessmentUseCase.Param(id, title, colorId));
    }

    @Test
    void testUpdateAssessmentParam_ColorIdIsNull_ErrorMessage() {
        UUID id = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new UpdateAssessmentUseCase.Param(id, "title", null));
        assertThat(throwable).hasMessage("colorId: " + UPDATE_ASSESSMENT_COLOR_ID_NOT_NULL);
    }

}
