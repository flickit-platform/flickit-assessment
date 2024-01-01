package org.flickit.assessment.core.application.service.assessment;


import org.flickit.assessment.core.application.domain.AssessmentColor;
import org.flickit.assessment.core.application.domain.QualityAttribute;
import org.flickit.assessment.core.application.domain.Subject;
import org.flickit.assessment.core.application.port.in.assessment.CreateAssessmentUseCase;
import org.flickit.assessment.core.application.port.in.assessment.CreateAssessmentUseCase.Param;
import org.flickit.assessment.core.application.port.out.assessment.CreateAssessmentPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.CreateAssessmentResultPort;
import org.flickit.assessment.core.application.port.out.qualityattributevalue.CreateQualityAttributeValuePort;
import org.flickit.assessment.core.application.port.out.subject.LoadSubjectPort;
import org.flickit.assessment.core.application.port.out.subjectvalue.CreateSubjectValuePort;
import org.flickit.assessment.core.test.fixture.application.QualityAttributeMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateAssessmentServiceTest {

    @InjectMocks
    private CreateAssessmentService service;

    @Mock
    private CreateAssessmentPort createAssessmentPort;

    @Mock
    private CreateAssessmentResultPort createAssessmentResultPort;

    @Mock
    private LoadSubjectPort loadSubjectsPort;

    @Mock
    private CreateSubjectValuePort createSubjectValuePort;

    @Mock
    private CreateQualityAttributeValuePort createQualityAttributeValuePort;


    @Test
    void testCreateAssessment_ValidParam_PersistsAndReturnsId() {
        Param param = new Param(
            1L,
            "title example",
            1L,
            1
        );
        UUID expectedId = UUID.randomUUID();
        when(createAssessmentPort.persist(any(CreateAssessmentPort.Param.class))).thenReturn(expectedId);
        List<Subject> expectedResponse = List.of();
        when(loadSubjectsPort.loadByKitIdWithAttributes(any())).thenReturn(expectedResponse);

        CreateAssessmentUseCase.Result result = service.createAssessment(param);
        assertEquals(expectedId, result.id());

        ArgumentCaptor<CreateAssessmentPort.Param> createPortParam = ArgumentCaptor.forClass(CreateAssessmentPort.Param.class);
        verify(createAssessmentPort).persist(createPortParam.capture());

        assertEquals("title-example", createPortParam.getValue().code());
        assertEquals(param.getTitle(), createPortParam.getValue().title());
        assertEquals(param.getAssessmentKitId(), createPortParam.getValue().assessmentKitId());
        assertEquals(param.getColorId(), createPortParam.getValue().colorId());
        assertNotNull(createPortParam.getValue().creationTime());
        assertNotNull(createPortParam.getValue().lastModificationTime());
    }

    @Test
    void testCreateAssessment_ValidParam_PersistsAssessmentResult() {
        Param param = new Param(
            1L,
            "title example",
            1L,
            1
        );
        UUID assessmentId = UUID.randomUUID();
        when(createAssessmentPort.persist(any(CreateAssessmentPort.Param.class))).thenReturn(assessmentId);
        UUID expectedResultId = UUID.randomUUID();
        when(createAssessmentResultPort.persist(any(CreateAssessmentResultPort.Param.class))).thenReturn(expectedResultId);
        List<Subject> expectedResponse = List.of();
        when(loadSubjectsPort.loadByKitIdWithAttributes(any())).thenReturn(expectedResponse);

        service.createAssessment(param);

        ArgumentCaptor<CreateAssessmentResultPort.Param> createPortParam = ArgumentCaptor.forClass(CreateAssessmentResultPort.Param.class);
        verify(createAssessmentResultPort).persist(createPortParam.capture());

        assertEquals(assessmentId, createPortParam.getValue().assessmentId());
        assertNotNull(createPortParam.getValue().lastModificationTime());
        assertFalse(createPortParam.getValue().isCalculateValid());
    }

    @Test
    void testCreateAssessment_ValidParam_PersistsSubjectValues() {
        Long assessmentKitId = 1L;
        Param param = new Param(
            1L,
            "title example",
            assessmentKitId,
            1
        );

        QualityAttribute qa1 = QualityAttributeMother.simpleAttribute();
        QualityAttribute qa2 = QualityAttributeMother.simpleAttribute();
        QualityAttribute qa3 = QualityAttributeMother.simpleAttribute();
        QualityAttribute qa4 = QualityAttributeMother.simpleAttribute();
        QualityAttribute qa5 = QualityAttributeMother.simpleAttribute();

        List<Subject> expectedSubjects = List.of(
            new Subject(1L, List.of(qa1, qa2)),
            new Subject(2L, List.of(qa3, qa4)),
            new Subject(3L, List.of(qa5))
        );
        when(loadSubjectsPort.loadByKitIdWithAttributes(assessmentKitId)).thenReturn(expectedSubjects);

        service.createAssessment(param);

        verify(createSubjectValuePort, times(1)).persistAll(anyList(), any());
    }

    @Test
    void testCreateAssessment_ValidCommand_PersistsQualityAttributeValue() {
        Long assessmentKitId = 1L;
        Param param = new Param(
            1L,
            "title example",
            assessmentKitId,
            1
        );
        QualityAttribute qa1 = QualityAttributeMother.simpleAttribute();
        QualityAttribute qa2 = QualityAttributeMother.simpleAttribute();
        QualityAttribute qa3 = QualityAttributeMother.simpleAttribute();
        QualityAttribute qa4 = QualityAttributeMother.simpleAttribute();
        QualityAttribute qa5 = QualityAttributeMother.simpleAttribute();

        List<Subject> expectedSubjects = List.of(
            new Subject(1L, List.of(qa1, qa2)),
            new Subject(2L, List.of(qa3, qa4)),
            new Subject(3L, List.of(qa5))
        );
        when(loadSubjectsPort.loadByKitIdWithAttributes(assessmentKitId)).thenReturn(expectedSubjects);

        service.createAssessment(param);

        verify(createQualityAttributeValuePort, times(1)).persistAll(anyList(), any());
    }

    @Test
    void testCreateAssessment_InvalidColor_UseDefaultColor() {
        Param param = new Param(
            1L,
            "title example",
            1L,
            7
        );
        List<Subject> expectedResponse = List.of();
        when(loadSubjectsPort.loadByKitIdWithAttributes(any())).thenReturn(expectedResponse);

        service.createAssessment(param);

        ArgumentCaptor<CreateAssessmentPort.Param> createPortParam = ArgumentCaptor.forClass(CreateAssessmentPort.Param.class);
        verify(createAssessmentPort).persist(createPortParam.capture());

        assertEquals(AssessmentColor.getDefault().getId(), createPortParam.getValue().colorId());
    }

}
