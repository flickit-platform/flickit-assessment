package org.flickit.assessment.core.application.service.assessment;

import org.flickit.assessment.core.application.domain.AssessmentColor;
import org.flickit.assessment.core.application.port.in.assessment.UpdateAssessmentUseCase;
import org.flickit.assessment.core.application.port.out.assessment.UpdateAssessmentPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
    void testUpdateAssessment_ValidParam_UpdatedAndReturnsId() {
        UUID id = UUID.randomUUID();
        UUID lastModifiedBy = UUID.randomUUID();

        when(updateAssessmentPort.update(any())).thenReturn(new UpdateAssessmentPort.Result(id));

        UpdateAssessmentUseCase.Param param = new UpdateAssessmentUseCase.Param(
            id,
            "new title",
            AssessmentColor.EMERALD.getId(),
            lastModifiedBy
        );
        UUID resultId = service.updateAssessment(param).id();
        assertEquals(id, resultId);

        ArgumentCaptor<UpdateAssessmentPort.AllParam> updatePortParam = ArgumentCaptor.forClass(UpdateAssessmentPort.AllParam.class);
        verify(updateAssessmentPort).update(updatePortParam.capture());

        assertEquals(param.getId(), updatePortParam.getValue().id());
        assertEquals(param.getTitle(), updatePortParam.getValue().title());
        assertEquals(param.getColorId(), updatePortParam.getValue().colorId());
        assertEquals(param.getLastModifiedBy(), updatePortParam.getValue().lastModifiedBy());
        assertNotNull(updatePortParam.getValue().title());
        assertNotNull(updatePortParam.getValue().colorId());
        assertNotNull(updatePortParam.getValue().lastModificationTime());
    }

    @Test
    void testUpdateAssessment_InvalidColor_UseDefaultColor() {
        UUID id = UUID.randomUUID();
        UUID lastModifiedBy = UUID.randomUUID();
        UpdateAssessmentUseCase.Param param = new UpdateAssessmentUseCase.Param(
            id,
            "title example",
            7,
            lastModifiedBy
        );
        when(updateAssessmentPort.update(any())).thenReturn(new UpdateAssessmentPort.Result(id));

        service.updateAssessment(param);

        ArgumentCaptor<UpdateAssessmentPort.AllParam> updatePortParam = ArgumentCaptor.forClass(UpdateAssessmentPort.AllParam.class);
        verify(updateAssessmentPort).update(updatePortParam.capture());

        assertEquals(AssessmentColor.getDefault().getId(), updatePortParam.getValue().colorId());
    }
}
