package org.flickit.flickitassessmentcore.application.service.assessment;

import org.flickit.flickitassessmentcore.application.port.in.assessment.GetAssessmentSpaceUseCase.Param;
import org.flickit.flickitassessmentcore.application.port.in.assessment.GetAssessmentSpaceUseCase.Result;
import org.flickit.flickitassessmentcore.application.port.out.assessment.LoadSpaceIdPort;
import org.flickit.flickitassessmentcore.application.service.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.flickit.flickitassessmentcore.common.ErrorMessageKey.GET_ASSESSMENT_SPACE_ID_ASSESSMENT_ID_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetAssessmentSpaceServiceTest {

    @InjectMocks
    private GetAssessmentSpaceService service;

    @Mock
    private LoadSpaceIdPort loadSpaceIdPort;

    @Test
    void getAssessmentSpaceId_validResult() {
        UUID assessmentId = UUID.randomUUID();
        Long spaceId = 1L;

        when(loadSpaceIdPort.loadSpaceIdByAssessmentId(assessmentId)).thenReturn(spaceId);

        Result assessmentSpace = service.getAssessmentSpace(new Param(assessmentId));

        ArgumentCaptor<UUID> assessmentIdArgument = ArgumentCaptor.forClass(UUID.class);
        verify(loadSpaceIdPort).loadSpaceIdByAssessmentId(assessmentIdArgument.capture());

        assertEquals(assessmentId, assessmentIdArgument.getValue());
        assertEquals(spaceId, assessmentSpace.spaceId());
        verify(loadSpaceIdPort, times(1)).loadSpaceIdByAssessmentId(any());
    }

    @Test
    void getAssessmentSpaceId_invalidAssessmentId() {
        UUID assessmentId = UUID.randomUUID();

        when(loadSpaceIdPort.loadSpaceIdByAssessmentId(assessmentId))
            .thenThrow(new ResourceNotFoundException(GET_ASSESSMENT_SPACE_ID_ASSESSMENT_ID_NOT_FOUND));

        assertThrows(ResourceNotFoundException.class, () -> service.getAssessmentSpace(new Param(assessmentId)));

        ArgumentCaptor<UUID> assessmentIdArgument = ArgumentCaptor.forClass(UUID.class);
        verify(loadSpaceIdPort).loadSpaceIdByAssessmentId(assessmentIdArgument.capture());

        assertEquals(assessmentId, assessmentIdArgument.getValue());
        verify(loadSpaceIdPort, times(1)).loadSpaceIdByAssessmentId(any());
    }
}
