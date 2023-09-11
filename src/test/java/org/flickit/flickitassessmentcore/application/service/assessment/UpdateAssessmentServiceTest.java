package org.flickit.flickitassessmentcore.application.service.assessment;

import jakarta.validation.ConstraintViolationException;
import org.flickit.flickitassessmentcore.application.domain.AssessmentColor;
import org.flickit.flickitassessmentcore.application.port.in.assessment.UpdateAssessmentUseCase;
import org.flickit.flickitassessmentcore.application.port.out.assessment.UpdateAssessmentPort;
import org.flickit.flickitassessmentcore.application.service.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.flickitassessmentcore.common.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateAssessmentServiceTest {

    @InjectMocks
    private UpdateAssessmentService service;

    @Mock
    private UpdateAssessmentPort updateAssessmentPort;

    @Test
    void updateAssessment_ValidParam_UpdatedAndReturnsId() {
        UUID id = UUID.randomUUID();

        when(updateAssessmentPort.update(any())).thenReturn(new UpdateAssessmentPort.Result(id));

        UpdateAssessmentUseCase.Param param = new UpdateAssessmentUseCase.Param(
            id,
            "new title",
            AssessmentColor.EMERALD.getId()
        );
        UUID resultId = service.updateAssessment(param).id();
        assertEquals(id, resultId);

        ArgumentCaptor<UpdateAssessmentPort.Param> updatePortParam = ArgumentCaptor.forClass(UpdateAssessmentPort.Param.class);
        verify(updateAssessmentPort).update(updatePortParam.capture());

        assertEquals(param.getId(), updatePortParam.getValue().id());
        assertEquals(param.getTitle(), updatePortParam.getValue().title());
        assertEquals(param.getColorId(), updatePortParam.getValue().colorId());
        assertNotNull(updatePortParam.getValue().title());
        assertNotNull(updatePortParam.getValue().colorId());
        assertNotNull(updatePortParam.getValue().lastModificationTime());
    }

    @Test
    void updateAssessment_NotExistingId_ErrorMessage() {
        when(updateAssessmentPort.update(any())).thenThrow(new ResourceNotFoundException(UPDATE_ASSESSMENT_ASSESSMENT_NOT_FOUND));
        UpdateAssessmentUseCase.Param param = new UpdateAssessmentUseCase.Param(
            UUID.randomUUID(), "title", AssessmentColor.BLUE.getId());
        var throwable = assertThrows(ResourceNotFoundException.class,
            () -> service.updateAssessment(param));
        assertThat(throwable).hasMessage(UPDATE_ASSESSMENT_ASSESSMENT_NOT_FOUND);
    }

    @Test
    void updateAssessment_NullId_ErrorMessage() {
        String title = "title";
        int colorId = AssessmentColor.BLUE.getId();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new UpdateAssessmentUseCase.Param(null, title, colorId));
        assertThat(throwable).hasMessage("id: " + UPDATE_ASSESSMENT_ID_NOT_NULL);
    }

    @Test
    void updateAssessment_InvalidTitle_ErrorMessage() {
        UUID id = UUID.randomUUID();
        int colorId = AssessmentColor.BLUE.getId();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new UpdateAssessmentUseCase.Param(id, "", colorId));
        assertThat(throwable).hasMessageContaining("title: " + UPDATE_ASSESSMENT_TITLE_SIZE_MIN);
        assertThat(throwable).hasMessageContaining("title: " + UPDATE_ASSESSMENT_TITLE_NOT_BLANK);
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
