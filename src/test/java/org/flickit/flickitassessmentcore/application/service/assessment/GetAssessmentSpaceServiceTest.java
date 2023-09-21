package org.flickit.flickitassessmentcore.application.service.assessment;

import org.flickit.flickitassessmentcore.application.port.in.assessment.GetAssessmentSpaceUseCase.Param;
import org.flickit.flickitassessmentcore.application.port.in.assessment.GetAssessmentSpaceUseCase.Result;
import org.flickit.flickitassessmentcore.application.port.out.assessment.GetAssessmentSpacePort;
import org.flickit.flickitassessmentcore.application.service.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.flickit.flickitassessmentcore.common.ErrorMessageKey.GET_ASSESSMENT_SPACE_ASSESSMENT_ID_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetAssessmentSpaceServiceTest {

    @InjectMocks
    private GetAssessmentSpaceService service;

    @Mock
    private GetAssessmentSpacePort getAssessmentSpacePort;

    @Test
    void getAssessmentSpaceId_validResult() {
        UUID assessmentId = UUID.randomUUID();
        Long spaceId = 1L;

        when(getAssessmentSpacePort.getSpaceIdByAssessmentId(assessmentId)).thenReturn(spaceId);

        Result assessmentSpace = service.getAssessmentSpace(new Param(assessmentId));

        ArgumentCaptor<UUID> assessmentIdArgument = ArgumentCaptor.forClass(UUID.class);
        verify(getAssessmentSpacePort).getSpaceIdByAssessmentId(assessmentIdArgument.capture());

        assertEquals(assessmentId, assessmentIdArgument.getValue());
        assertEquals(spaceId, assessmentSpace.spaceId());
        verify(getAssessmentSpacePort, times(1)).getSpaceIdByAssessmentId(any());
    }

    @Test
    void getAssessmentSpaceId_invalidAssessmentId() {
        UUID assessmentId = UUID.randomUUID();

        when(getAssessmentSpacePort.getSpaceIdByAssessmentId(assessmentId))
            .thenThrow(new ResourceNotFoundException(GET_ASSESSMENT_SPACE_ASSESSMENT_ID_NOT_FOUND));

        Param param = new Param(assessmentId);
        assertThrows(ResourceNotFoundException.class, () -> service.getAssessmentSpace(param));

        ArgumentCaptor<UUID> assessmentIdArgument = ArgumentCaptor.forClass(UUID.class);
        verify(getAssessmentSpacePort).getSpaceIdByAssessmentId(assessmentIdArgument.capture());

        assertEquals(assessmentId, assessmentIdArgument.getValue());
        verify(getAssessmentSpacePort, times(1)).getSpaceIdByAssessmentId(any());
    }
}
