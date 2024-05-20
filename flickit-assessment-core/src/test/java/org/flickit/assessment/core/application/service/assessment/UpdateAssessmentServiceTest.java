package org.flickit.assessment.core.application.service.assessment;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.core.application.domain.AssessmentColor;
import org.flickit.assessment.core.application.port.in.assessment.UpdateAssessmentUseCase;
import org.flickit.assessment.core.application.port.out.assessment.CheckUserAssessmentAccessPort;
import org.flickit.assessment.core.application.port.out.assessment.UpdateAssessmentPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
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

    @Mock
    private CheckUserAssessmentAccessPort checkUserAssessmentAccessPort;

    @Test
    void testUpdateAssessment_ValidParam_UpdatedAndReturnsId() {
        UUID id = UUID.randomUUID();
        UUID currentUserId = UUID.randomUUID();

        when(checkUserAssessmentAccessPort.hasAccess(id, currentUserId)).thenReturn(true);
        when(updateAssessmentPort.update(any())).thenReturn(new UpdateAssessmentPort.Result(id));

        UpdateAssessmentUseCase.Param param = new UpdateAssessmentUseCase.Param(
            id,
            "new title",
            AssessmentColor.EMERALD.getId(),
            currentUserId
        );
        UUID resultId = service.updateAssessment(param).id();
        assertEquals(id, resultId);

        ArgumentCaptor<UpdateAssessmentPort.AllParam> updatePortParam = ArgumentCaptor.forClass(UpdateAssessmentPort.AllParam.class);
        verify(updateAssessmentPort).update(updatePortParam.capture());

        assertEquals(param.getId(), updatePortParam.getValue().id());
        assertEquals(param.getTitle(), updatePortParam.getValue().title());
        assertEquals(param.getColorId(), updatePortParam.getValue().colorId());
        assertEquals(param.getCurrentUserId(), updatePortParam.getValue().lastModifiedBy());
        assertNotNull(updatePortParam.getValue().title());
        assertNotNull(updatePortParam.getValue().colorId());
        assertNotNull(updatePortParam.getValue().lastModificationTime());
    }

    @Test
    void testUpdateAssessment_InvalidColor_UseDefaultColor() {
        UUID id = UUID.randomUUID();
        UUID currentUserId = UUID.randomUUID();
        UpdateAssessmentUseCase.Param param = new UpdateAssessmentUseCase.Param(
            id,
            "title example",
            7,
            currentUserId
        );
        when(checkUserAssessmentAccessPort.hasAccess(id, currentUserId)).thenReturn(true);
        when(updateAssessmentPort.update(any())).thenReturn(new UpdateAssessmentPort.Result(id));

        service.updateAssessment(param);

        ArgumentCaptor<UpdateAssessmentPort.AllParam> updatePortParam = ArgumentCaptor.forClass(UpdateAssessmentPort.AllParam.class);
        verify(updateAssessmentPort).update(updatePortParam.capture());

        assertEquals(AssessmentColor.getDefault().getId(), updatePortParam.getValue().colorId());
    }

    @Test
    void testUpdateAssessment_UserHasNoAccessToAssessment_ThrowAccessDeniedException() {
        UUID id = UUID.randomUUID();
        UUID currentUserId = UUID.randomUUID();

        when(checkUserAssessmentAccessPort.hasAccess(id, currentUserId)).thenReturn(false);

        UpdateAssessmentUseCase.Param param = new UpdateAssessmentUseCase.Param(
            id,
            "new title",
            AssessmentColor.EMERALD.getId(),
            currentUserId
        );

        var throwable = assertThrows(AccessDeniedException.class, () -> service.updateAssessment(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());
    }
}
