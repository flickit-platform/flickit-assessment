package org.flickit.flickitassessmentcore.application.service.assessment;


import org.flickit.flickitassessmentcore.application.port.in.assessment.CreateAssessmentCommand;
import org.flickit.flickitassessmentcore.application.port.out.assessment.CreateAssessmentPort;
import org.flickit.flickitassessmentcore.application.port.out.assessmentresult.CreateAssessmentResultPort;
import org.flickit.flickitassessmentcore.application.port.out.assessmentsubject.LoadAssessmentSubjectIdsByAssessmentKitPort;
import org.flickit.flickitassessmentcore.application.port.out.assessmentsubjectvalue.CreateAssessmentSubjectValuePort;
import org.flickit.flickitassessmentcore.application.port.out.qualityattribute.LoadQualityAttributeIdsByAssessmentSubjectPort;
import org.flickit.flickitassessmentcore.domain.AssessmentColor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateAssessmentServiceTest {

    @Spy
    @InjectMocks
    private CreateAssessmentService service;

    @Mock
    private CreateAssessmentPort createAssessmentPort;

    @Mock
    private CreateAssessmentResultPort createAssessmentResultPort;

    @Mock
    private LoadAssessmentSubjectIdsByAssessmentKitPort loadAssessmentSubjectIdsPort;

    @Mock
    private CreateAssessmentSubjectValuePort createAssessmentSubjectValuePort;

    @Mock
    private LoadQualityAttributeIdsByAssessmentSubjectPort loadQualityAttributeIdsPort;

    @Mock
    private org.flickit.flickitassessmentcore.application.port.out.qualityattributevalue.CreateQualityAttributeValuePort createQualityAttributeValuePort;


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
    void createAssessment_ValidCommand_PersistsAssessmentResult() {
        CreateAssessmentCommand command = new CreateAssessmentCommand(
            1L,
            "title example",
            1L,
            1
        );
        UUID assessmentId = UUID.randomUUID();
        doReturn(assessmentId).when(createAssessmentPort).persist(any(CreateAssessmentPort.Param.class));
        UUID expectedResultId = UUID.randomUUID();
        doReturn(expectedResultId).when(createAssessmentResultPort).persist(any(CreateAssessmentResultPort.Param.class));

        service.createAssessment(command);

        ArgumentCaptor<CreateAssessmentResultPort.Param> param = ArgumentCaptor.forClass(CreateAssessmentResultPort.Param.class);
        verify(createAssessmentResultPort).persist(param.capture());

        assertEquals(assessmentId, param.getValue().assessmentId());
        assertFalse(param.getValue().isValid());
    }

    @Test
    void createAssessment_ValidCommand_PersistsAssessmentSubjectValues() {
        Long assessmentKitId = 1L;
        CreateAssessmentCommand command = new CreateAssessmentCommand(
            1L,
            "title example",
            assessmentKitId,
            1
        );
        List<Long> expectedAssessmentSubjectIds = Arrays.asList(1L, 2L, 3L);
        doReturn(expectedAssessmentSubjectIds).when(loadAssessmentSubjectIdsPort).loadIdsByAssessmentKitId(assessmentKitId);

        service.createAssessment(command);

        verify(createAssessmentSubjectValuePort, times(1)).persistAllWithAssessmentResultId(anyList(), any());
    }

    @Test
    void createAssessment_ValidCommand_PersistsQualityAttributeValue() {
        Long assessmentKitId = 1L;
        CreateAssessmentCommand command = new CreateAssessmentCommand(
            1L,
            "title example",
            assessmentKitId,
            1
        );
        List<Long> expectedAssessmentSubjectIds = Arrays.asList(1L, 2L, 3L);
        doReturn(expectedAssessmentSubjectIds).when(loadAssessmentSubjectIdsPort).loadIdsByAssessmentKitId(assessmentKitId);
        List<Long> expectedQualityAttributeIds = Arrays.asList(1L, 2L, 3L, 4L, 5L);
        doReturn(expectedQualityAttributeIds).when(loadQualityAttributeIdsPort).loadIdsByAssessmentSubjectId(anyLong());

        service.createAssessment(command);

        verify(loadQualityAttributeIdsPort, times(expectedAssessmentSubjectIds.size())).loadIdsByAssessmentSubjectId(anyLong());
        verify(createQualityAttributeValuePort, times(expectedAssessmentSubjectIds.size())).persistAllWithAssessmentResultId(anyList(), any());
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
