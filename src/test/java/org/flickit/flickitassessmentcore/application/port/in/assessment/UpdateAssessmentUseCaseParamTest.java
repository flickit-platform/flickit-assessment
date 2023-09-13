package org.flickit.flickitassessmentcore.application.port.in.assessment;

import jakarta.validation.ConstraintViolationException;
import org.flickit.flickitassessmentcore.application.domain.AssessmentColor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.flickitassessmentcore.common.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class UpdateAssessmentUseCaseParamTest {

    @Test
    void updateAssessment_NullId_ErrorMessage() {
        String title = "title";
        int colorId = AssessmentColor.BLUE.getId();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new UpdateAssessmentUseCase.Param(null, title, colorId));
        assertThat(throwable).hasMessage("id: " + UPDATE_ASSESSMENT_ID_NOT_NULL);
    }

    @Test
    void updateAssessment_BlankTitle_ErrorMessage() {
        UUID id = UUID.randomUUID();
        int colorId = AssessmentColor.BLUE.getId();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new UpdateAssessmentUseCase.Param(id, "    ", colorId));
        assertThat(throwable).hasMessage("title: " + UPDATE_ASSESSMENT_TITLE_NOT_BLANK);
    }

    @Test
    void updateAssessment_InvalidTitleMinSize_ErrorMessage() {
        UUID id = UUID.randomUUID();
        int colorId = AssessmentColor.BLUE.getId();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new UpdateAssessmentUseCase.Param(id, "ab", colorId));
        assertThat(throwable).hasMessage("title: " + UPDATE_ASSESSMENT_TITLE_SIZE_MIN);
    }

    @Test
    void updateAssessment_NullColorId_ErrorMessage() {
        UUID id = UUID.randomUUID();
        String title = "title";
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new UpdateAssessmentUseCase.Param(id, title, null));
        assertThat(throwable).hasMessage("colorId: " + UPDATE_ASSESSMENT_COLOR_ID_NOT_NULL);
    }

}
