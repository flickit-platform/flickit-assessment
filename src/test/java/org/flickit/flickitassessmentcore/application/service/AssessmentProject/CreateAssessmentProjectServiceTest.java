package org.flickit.flickitassessmentcore.application.service.AssessmentProject;


import org.flickit.flickitassessmentcore.application.port.in.AssessmentProject.AssessmentColorDto;
import org.flickit.flickitassessmentcore.application.port.in.AssessmentProject.CreateAssessmentProjectCommand;
import org.flickit.flickitassessmentcore.application.port.out.AssessmentColor.LoadAssessmentColorByIdPort;
import org.flickit.flickitassessmentcore.application.port.out.AssessmentProject.CreateAssessmentProjectPort;
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
class CreateAssessmentProjectServiceTest {

    @Spy
    @InjectMocks
    private CreateAssessmentProjectService createAssessmentProjectService;
    @Mock
    private CreateAssessmentProjectPort createAssessmentProjectPort;
    @Mock
    private LoadAssessmentColorByIdPort loadAssessmentColorByIdPort;

    @Test
    void createAssessmentProject_ValidCommand_PersistsAndReturnsId() {
        CreateAssessmentProjectCommand command = createValidCommand();
        UUID expectedId = UUID.randomUUID();
        when(createAssessmentProjectPort.persist(any(CreateAssessmentProjectCommand.class))).thenReturn(expectedId);
        when(loadAssessmentColorByIdPort.loadById(anyLong())).thenReturn(new AssessmentColorDto(1L));

        UUID actualId = createAssessmentProjectService.createAssessmentProject(command);

        assertEquals(expectedId, actualId);
        verify(createAssessmentProjectPort, times(1)).persist(any(CreateAssessmentProjectCommand.class));
        verify(loadAssessmentColorByIdPort, times(1)).loadById(anyLong());
    }

    @Test
    void createAssessmentProject_InvalidColor_ThrowsException() {
        CreateAssessmentProjectCommand command = createValidCommand();
        when(loadAssessmentColorByIdPort.loadById(anyLong())).thenReturn(null);

        assertThrows(AssessmentColorNotFoundException.class, () -> {
            createAssessmentProjectService.createAssessmentProject(command);
        });
        verify(createAssessmentProjectPort, never()).persist(any(CreateAssessmentProjectCommand.class));
    }


    private static CreateAssessmentProjectCommand createValidCommand() {
        return new CreateAssessmentProjectCommand(
            "title example",
            "description example",
            1L,
            1L,
            1L
        );
    }

}
