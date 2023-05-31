package org.flickit.flickitassessmentcore.application.service.assessment;


import org.flickit.flickitassessmentcore.application.port.in.assessment.AssessmentColorDto;
import org.flickit.flickitassessmentcore.application.port.in.assessment.CreateAssessmentCommand;
import org.flickit.flickitassessmentcore.application.port.out.assessmentcolor.LoadAssessmentColorByIdPort;
import org.flickit.flickitassessmentcore.application.port.out.assessment.CreateAssessmentPort;
import org.flickit.flickitassessmentcore.application.service.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateAssessmentServiceTest {

    @Spy
    @InjectMocks
    private CreateAssessmentService createAssessmentService;
    @Mock
    private CreateAssessmentPort createAssessmentPort;
    @Mock
    private LoadAssessmentColorByIdPort loadAssessmentColorByIdPort;

    @Test
    void createAssessment_ValidCommand_PersistsAndReturnsId() {
        CreateAssessmentCommand command = createValidCommand();
        UUID expectedId = UUID.randomUUID();
        when(createAssessmentPort.persist(any(CreateAssessmentCommand.class))).thenReturn(expectedId);
        when(loadAssessmentColorByIdPort.loadById(anyLong())).thenReturn(new AssessmentColorDto(1L));

        UUID actualId = createAssessmentService.createAssessment(command);

        assertEquals(expectedId, actualId);
        verify(createAssessmentPort, times(1)).persist(any(CreateAssessmentCommand.class));
        verify(loadAssessmentColorByIdPort, times(1)).loadById(anyLong());
    }

    @Test
    void createAssessment_InvalidColor_ThrowsException() {
        CreateAssessmentCommand command = createValidCommand();
        when(loadAssessmentColorByIdPort.loadById(anyLong())).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> {
            createAssessmentService.createAssessment(command);
        });
        verify(createAssessmentPort, never()).persist(any(CreateAssessmentCommand.class));
    }


    private static CreateAssessmentCommand createValidCommand() {
        return new CreateAssessmentCommand(
            "title example",
            "description example",
            1L,
            1L,
            1L
        );
    }

}
