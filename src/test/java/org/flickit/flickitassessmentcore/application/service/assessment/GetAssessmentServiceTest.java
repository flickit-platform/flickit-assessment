package org.flickit.flickitassessmentcore.application.service.assessment;

import org.flickit.flickitassessmentcore.application.domain.Assessment;
import org.flickit.flickitassessmentcore.application.domain.mother.AssessmentMother;
import org.flickit.flickitassessmentcore.application.port.in.assessment.GetAssessmentUseCase.Param;
import org.flickit.flickitassessmentcore.application.port.in.assessment.GetAssessmentUseCase.Result;
import org.flickit.flickitassessmentcore.application.port.out.assessment.GetAssessmentPort;
import org.flickit.flickitassessmentcore.application.service.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetAssessmentServiceTest {

    @InjectMocks
    private GetAssessmentService service;

    @Mock
    private GetAssessmentPort getAssessmentPort;

    @Test
    void testGetAssessment_validResult() {
        Assessment assessment = AssessmentMother.assessment();
        UUID assessmentId = assessment.getId();

        when(getAssessmentPort.getAssessmentById(assessmentId)).thenReturn(Optional.of(assessment));

        Result result = service.getAssessment(new Param(assessmentId));

        ArgumentCaptor<UUID> assessmentIdArgument = ArgumentCaptor.forClass(UUID.class);
        verify(getAssessmentPort).getAssessmentById(assessmentIdArgument.capture());

        assertEquals(assessmentId, assessmentIdArgument.getValue());
        assertEquals(assessment.getSpaceId(), result.spaceId());
        assertEquals(assessment.getAssessmentKit().getId(), result.kitId());
        verify(getAssessmentPort, times(1)).getAssessmentById(any());
    }

    @Test
    void getAssessment_invalidAssessmentId() {
        UUID assessmentId = UUID.randomUUID();

        when(getAssessmentPort.getAssessmentById(assessmentId))
            .thenReturn(Optional.empty());

        Param param = new Param(assessmentId);
        assertThrows(ResourceNotFoundException.class, () -> service.getAssessment(param));

        ArgumentCaptor<UUID> assessmentIdArgument = ArgumentCaptor.forClass(UUID.class);
        verify(getAssessmentPort).getAssessmentById(assessmentIdArgument.capture());

        assertEquals(assessmentId, assessmentIdArgument.getValue());
        verify(getAssessmentPort, times(1)).getAssessmentById(any());
    }
}
