package org.flickit.flickitassessmentcore.application.service.assessment;

import jakarta.validation.ConstraintViolationException;
import org.flickit.flickitassessmentcore.application.port.in.assessment.UpdateAssessmentUseCase;
import org.flickit.flickitassessmentcore.application.port.out.assessment.LoadAssessmentPort;
import org.flickit.flickitassessmentcore.application.port.out.assessment.SaveAssessmentPort;
import org.flickit.flickitassessmentcore.application.service.exception.ResourceNotFoundException;
import org.flickit.flickitassessmentcore.domain.Assessment;
import org.flickit.flickitassessmentcore.domain.AssessmentColor;
import org.flickit.flickitassessmentcore.domain.AssessmentKit;
import org.flickit.flickitassessmentcore.domain.mother.AssessmentKitMother;
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
public class UpdateAssessmentServiceTest {

    @Spy
    @InjectMocks
    private UpdateAssessmentService service;

    @Mock
    private LoadAssessmentPort loadAssessmentPort;

    @Mock
    private SaveAssessmentPort saveAssessmentPort;

    @Test
    void updateAssessment_ValidParam_UpdatedAndReturnsId() {
        UUID id = UUID.randomUUID();
        AssessmentKit kit = AssessmentKitMother.kit();
        Assessment loadedAssessment = new Assessment(
            id,
            "code",
            "title",
            kit,
            AssessmentColor.BLUE.getId(),
            1L,
            LocalDateTime.now(),
            LocalDateTime.now()
        );
        when(loadAssessmentPort.loadAssessment(id)).thenReturn(new LoadAssessmentPort.Result(loadedAssessment));
        when(saveAssessmentPort.saveAssessment(any())).thenReturn(new SaveAssessmentPort.Result(id));

        UpdateAssessmentUseCase.Param param = new UpdateAssessmentUseCase.Param(
            id,
            "new title",
            AssessmentColor.EMERALD.getId()
        );
        UUID resultId = service.updateAssessment(param).id();
        assertEquals(id, resultId);

        ArgumentCaptor<SaveAssessmentPort.Param> savePortParam = ArgumentCaptor.forClass(SaveAssessmentPort.Param.class);
        verify(saveAssessmentPort).saveAssessment(savePortParam.capture());

        assertEquals(param.getId(), savePortParam.getValue().assessment().getId());
        assertEquals(param.getTitle(), savePortParam.getValue().assessment().getTitle());
        assertEquals(param.getColorId(), savePortParam.getValue().assessment().getColorId());
        assertNotNull(savePortParam.getValue().assessment().getCreationTime());
        assertNotNull(savePortParam.getValue().assessment().getLastModificationTime());
        assertNotNull(savePortParam.getValue().assessment().getSpaceId());
        assertNotNull(savePortParam.getValue().assessment().getCode());
    }

    @Test
    void updateAssessment_NotExistingId_ErrorMessage() {
        when(loadAssessmentPort.loadAssessment(any())).thenThrow(ResourceNotFoundException.class);
        assertThrows(ResourceNotFoundException.class,
            () -> service.updateAssessment(new UpdateAssessmentUseCase.Param(
                UUID.randomUUID(), "title", AssessmentColor.BLUE.getId()
            )), UPDATE_ASSESSMENT_ASSESSMENT_NOT_FOUND);
    }

    @Test
    void updateAssessment_NullId_ErrorMessage() {
        assertThrows(ConstraintViolationException.class,
            () -> service.updateAssessment(new UpdateAssessmentUseCase.Param(
                null, "title", AssessmentColor.BLUE.getId()
            )), UPDATE_ASSESSMENT_ID_NOT_NULL);
    }

    @Test
    void updateAssessment_InvalidTitle_ErrorMessage() {
        assertThrows(ConstraintViolationException.class,
            () -> service.updateAssessment(new UpdateAssessmentUseCase.Param(
                UUID.randomUUID(), "", AssessmentColor.BLUE.getId()
            )), UPDATE_ASSESSMENT_TITLE_NOT_BLANK);
    }

    @Test
    void updateAssessment_NullColorId_ErrorMessage() {
        assertThrows(ConstraintViolationException.class,
            () -> service.updateAssessment(new UpdateAssessmentUseCase.Param(
                UUID.randomUUID(), "title", null
            )), UPDATE_ASSESSMENT_COLOR_ID_NOT_NULL);
    }
}
