package org.flickit.flickitassessmentcore.application.service.assessment;


import org.flickit.flickitassessmentcore.application.port.out.assessment.CreateAssessmentPort;
import org.flickit.flickitassessmentcore.application.port.in.assessment.CreateAssessmentCommand;
import org.flickit.flickitassessmentcore.domain.AssessmentColor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateAssessmentServiceTest {

    @Spy
    @InjectMocks
    private org.flickit.flickitassessmentcore.application.service.assessment.CreateAssessmentService service;

    @Mock
    private CreateAssessmentPort createAssessmentPort;


    @Test
    void createAssessment_ValidCommand_PersistsAndReturnsId() {
        CreateAssessmentCommand command = new CreateAssessmentCommand(
            1L,
            "title example",
            1L,
            1
        );
        UUID expectedId = UUID.randomUUID();
        doReturn(expectedId).when(createAssessmentPort).persist(any(CreateAssessmentPort.Param.class));

        UUID actualId = service.createAssessment(command);
        assertEquals(expectedId, actualId);

        ArgumentCaptor<CreateAssessmentPort.Param> param = ArgumentCaptor.forClass(CreateAssessmentPort.Param.class);
        verify(createAssessmentPort).persist(param.capture());

        assertEquals(command.getTitle(), param.getValue().title());
        assertEquals(command.getAssessmentKitId(), param.getValue().assessmentKitId());
        assertEquals(command.getColorId(), param.getValue().colorId());
        assertEquals("title-example", param.getValue().code());
        assertNotNull(param.getValue().creationTime());
        assertNotNull(param.getValue().lastModificationDate());
    }

    @Test
    void createAssessment_NullColor_UseDefaultColor() {
        CreateAssessmentCommand command = new CreateAssessmentCommand(
            1L,
            "title example",
            1L,
            null
        );

        service.createAssessment(command);

        ArgumentCaptor<CreateAssessmentPort.Param> param = ArgumentCaptor.forClass(CreateAssessmentPort.Param.class);
        verify(createAssessmentPort).persist(param.capture());

        assertEquals(AssessmentColor.getDefault().getId(), param.getValue().colorId());
    }

    @Test
    void createAssessment_InvalidColor_UseDefaultColor() {
        CreateAssessmentCommand command = new CreateAssessmentCommand(
            1L,
            "title example",
            1L,
            7
        );

        service.createAssessment(command);

        ArgumentCaptor<CreateAssessmentPort.Param> param = ArgumentCaptor.forClass(CreateAssessmentPort.Param.class);
        verify(createAssessmentPort).persist(param.capture());

        assertEquals(AssessmentColor.getDefault().getId(), param.getValue().colorId());
    }

    @Test
    void generateSlugCode_NoWhitespace_ReturnsLowerCaseCode() {
        String title = "ExampleTitle";

        String code = ReflectionTestUtils.invokeMethod(service, "generateSlugCode", title);

        assertEquals("exampletitle", code);
    }

    @Test
    void generateSlugCode_WithWhitespace_ReturnsLowerCaseCodeWithHyphens() {
        String title = "Example Title with Whitespace";

        String code = ReflectionTestUtils.invokeMethod(service, "generateSlugCode", title);

        assertEquals("example-title-with-whitespace", code);
    }

    @Test
    void generateSlugCode_WithLeadingAndTrailingWhitespace_ReturnsLowerCaseCodeWithHyphens() {
        String title = "  Example Title with   Leading and Trailing   Whitespace  ";

        String code = ReflectionTestUtils.invokeMethod(service, "generateSlugCode", title);

        assertEquals("example-title-with-leading-and-trailing-whitespace", code);
    }

}
