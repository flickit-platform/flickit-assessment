package org.flickit.flickitassessmentcore.application.service.assessment;

import jakarta.validation.ConstraintViolationException;
import org.flickit.flickitassessmentcore.application.port.in.assessment.EditAssessmentUseCase;
import org.flickit.flickitassessmentcore.application.port.out.assessment.LoadAssessmentPort;
import org.flickit.flickitassessmentcore.application.port.out.assessment.SaveAssessmentPort;
import org.flickit.flickitassessmentcore.application.service.exception.ResourceNotFoundException;
import org.flickit.flickitassessmentcore.domain.Assessment;
import org.flickit.flickitassessmentcore.domain.AssessmentColor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.flickit.flickitassessmentcore.common.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EditAssessmentServiceTest {

    @Spy
    @InjectMocks
    private EditAssessmentService service;

    @Mock
    private LoadAssessmentPort loadAssessmentPort;

    @Mock
    private SaveAssessmentPort saveAssessmentPort;

    @Test
    void editAssessment_ValidParam_UpdatedAndReturnsId() {
        UUID id = UUID.randomUUID();
        Assessment loadedAssessment = new Assessment(
            id,
            "code",
            "title",
            LocalDateTime.now(),
            LocalDateTime.now(),
            1L,
            AssessmentColor.BLUE.getId(),
            1L
        );
        when(loadAssessmentPort.loadAssessment(id)).thenReturn(new LoadAssessmentPort.Result(loadedAssessment));
        when(saveAssessmentPort.saveAssessment(any())).thenReturn(new SaveAssessmentPort.Result(id));

        EditAssessmentUseCase.Param param = new EditAssessmentUseCase.Param(
            id,
            "new title",
            2L,
            AssessmentColor.EMERALD.getId()
        );
        UUID resultId = service.editAssessment(param).id();
        assertEquals(id, resultId);

        ArgumentCaptor<SaveAssessmentPort.Param> savePortParam = ArgumentCaptor.forClass(SaveAssessmentPort.Param.class);
        verify(saveAssessmentPort).saveAssessment(savePortParam.capture());

        assertEquals(param.getId(), savePortParam.getValue().assessment().getId());
        assertEquals(param.getTitle(), savePortParam.getValue().assessment().getTitle());
        assertEquals(param.getAssessmentKitId(), savePortParam.getValue().assessment().getAssessmentKitId());
        assertEquals(param.getColorId(), savePortParam.getValue().assessment().getColorId());
        assertNotNull(savePortParam.getValue().assessment().getCreationTime());
        assertNotNull(savePortParam.getValue().assessment().getLastModificationDate());
        assertNotNull(savePortParam.getValue().assessment().getSpaceId());
        assertNotNull(savePortParam.getValue().assessment().getCode());
    }

    @Test
    void editAssessment_NotExistingId_ErrorMessage() {
        when(loadAssessmentPort.loadAssessment(any())).thenThrow(ResourceNotFoundException.class);
        assertThrows(ResourceNotFoundException.class,
            () -> service.editAssessment(new EditAssessmentUseCase.Param(
                UUID.randomUUID(), "title", 1L, AssessmentColor.BLUE.getId()
            )), EDIT_ASSESSMENT_ASSESSMENT_NOT_FOUND);
    }

    @Test
    void editAssessment_NullId_ErrorMessage() {
        assertThrows(ConstraintViolationException.class,
            () -> service.editAssessment(new EditAssessmentUseCase.Param(
                null, "title", 1L, AssessmentColor.BLUE.getId()
            )), EDIT_ASSESSMENT_ID_NOT_NULL);
    }

    @Test
    void editAssessment_InvalidTitle_ErrorMessage() {
        assertThrows(ConstraintViolationException.class,
            () -> service.editAssessment(new EditAssessmentUseCase.Param(
                UUID.randomUUID(), "", 1L, AssessmentColor.BLUE.getId()
            )), EDIT_ASSESSMENT_TITLE_NOT_BLANK);
    }

    @Test
    void editAssessment_NullAssessmentId_ErrorMessage() {
        assertThrows(ConstraintViolationException.class,
            () -> service.editAssessment(new EditAssessmentUseCase.Param(
                UUID.randomUUID(), "title", null, AssessmentColor.BLUE.getId()
            )), EDIT_ASSESSMENT_ASSESSMENT_NOT_FOUND);
    }

    @Test
    void editAssessment_NullColorId_ErrorMessage() {
        assertThrows(ConstraintViolationException.class,
            () -> service.editAssessment(new EditAssessmentUseCase.Param(
                UUID.randomUUID(), "title", 1L, null
            )), EDIT_ASSESSMENT_COLOR_ID_NOT_NULL);
    }
}
