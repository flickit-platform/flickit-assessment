package org.flickit.flickitassessmentcore.application.service.assessment;

import org.flickit.flickitassessmentcore.application.domain.AssessmentColor;
import org.flickit.flickitassessmentcore.application.port.in.assessment.UpdateAssessmentUseCase;
import org.flickit.flickitassessmentcore.application.port.out.assessment.CheckAssessmentExistencePort;
import org.flickit.flickitassessmentcore.application.port.out.assessment.UpdateAssessmentPort;
import org.flickit.flickitassessmentcore.application.service.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateAssessmentServiceTest {

    @InjectMocks
    private UpdateAssessmentService service;

    @Mock
    private UpdateAssessmentPort updateAssessmentPort;

    @Mock
    private CheckAssessmentExistencePort checkAssessmentExistencePort;

    @Test
    void updateAssessment_ValidParam_UpdatedAndReturnsId() {
        UUID id = UUID.randomUUID();

        when(checkAssessmentExistencePort.existsById(id)).thenReturn(true);
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
    void updateAssessment_InvalidAssessmentId_ThrowNotFoundException() {
        UUID id = UUID.randomUUID();

        when(checkAssessmentExistencePort.existsById(id)).thenReturn(false);

        UpdateAssessmentUseCase.Param param = new UpdateAssessmentUseCase.Param(
            id,
            "new title",
            AssessmentColor.EMERALD.getId()
        );
        assertThrows(ResourceNotFoundException.class, () -> service.updateAssessment(param));

        ArgumentCaptor<UUID> assessmentIdCapture = ArgumentCaptor.forClass(UUID.class);
        verify(checkAssessmentExistencePort).existsById(assessmentIdCapture.capture());

        assertEquals(param.getId(), assessmentIdCapture.getValue());
        verify(updateAssessmentPort, never()).update(any());
    }
}
