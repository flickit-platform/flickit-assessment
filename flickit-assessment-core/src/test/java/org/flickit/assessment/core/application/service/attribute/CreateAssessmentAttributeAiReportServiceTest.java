package org.flickit.assessment.core.application.service.attribute;

import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.port.in.attribute.CreateAssessmentAttributeAiReportUseCase.Param;
import org.flickit.assessment.core.application.port.out.assessment.GetAssessmentPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.core.application.port.out.attribute.CreateAssessmentAttributeAiPort;
import org.flickit.assessment.core.application.port.out.attribute.LoadAttributePort;
import org.flickit.assessment.core.application.port.out.attributeinsight.CreateAttributeInsightPort;
import org.flickit.assessment.core.application.port.out.attributeinsight.LoadAttributeInsightPort;
import org.flickit.assessment.core.application.port.out.attributeinsight.UpdateAttributeInsightPort;
import org.flickit.assessment.core.test.fixture.application.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Optional;
import java.util.UUID;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.EXPORT_ASSESSMENT_REPORT;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.core.common.ErrorMessageKey.ASSESSMENT_ID_NOT_FOUND;
import static org.flickit.assessment.core.common.ErrorMessageKey.CREATE_ASSESSMENT_ATTRIBUTE_AI_REPORT_ASSESSMENT_RESULT_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateAssessmentAttributeAiReportServiceTest {

    private CreateAssessmentAttributeAiReportService service;

    @Mock
    private GetAssessmentPort getAssessmentPort;

    @Mock
    private AssessmentAccessChecker assessmentAccessChecker;

    @Mock
    private LoadAssessmentResultPort loadAssessmentResultPort;

    @Mock
    private CreateAssessmentAttributeAiPort createAssessmentAttributeAiPort;

    @Mock
    private UpdateAttributeInsightPort updateAttributeInsightPort;

    @Mock
    private LoadAttributePort loadAttributePort;

    @Mock
    private LoadAttributeInsightPort loadAttributeInsightPort;

    @Mock
    private CreateAttributeInsightPort createAttributeInsightPort;

    private final String fileLink = "http://127.0.0.1:9000/report/5e3b5d74-cc9c-4b54-b051-86e934ae9a03/temp.?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-" +
        "Credential=minioadmin%2F20240726%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20240726T052101Z&X-Amz-Expires=604800&X-Amz-SignedHeaders=host&X-Amz-" +
        "Signature=8dfab4d27ab012f1ef15beb58b54da353049f00b9e4a53115eb385b41fb4f4a5";

    @BeforeEach
    void prepare() {
        service = spy(new CreateAssessmentAttributeAiReportService(loadAttributePort, getAssessmentPort,
            assessmentAccessChecker, loadAssessmentResultPort, loadAttributeInsightPort, createAttributeInsightPort, updateAttributeInsightPort, createAssessmentAttributeAiPort));
    }

    @Test
    @DisplayName("Create Assessment Attribute AI Report - Assessment Not Found - Throw ResourceNotFoundException")
    void testCreateAssessmentAttributeAiReport_AssessmentNotFound_ThrowResourceNotFoundException() {
        UUID assessmentId = UUID.randomUUID();
        Long attributeId = 1L;
        UUID currentUserId = UUID.randomUUID();
        Param param = new Param(assessmentId, attributeId, fileLink, currentUserId);

        when(getAssessmentPort.getAssessmentById(param.getAssessmentId())).thenReturn(Optional.empty());

        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.createAttributeAiReport(param));
        assertEquals(ASSESSMENT_ID_NOT_FOUND, throwable.getMessage());

        verify(getAssessmentPort).getAssessmentById(param.getAssessmentId());
        verifyNoInteractions(assessmentAccessChecker,
            loadAttributePort,
            loadAssessmentResultPort,
            loadAttributeInsightPort,
            createAssessmentAttributeAiPort,
            updateAttributeInsightPort);
    }

    @Test
    @DisplayName("Create Assessment Attribute AI Report - User Lacks Required Permission - Throw AccessDeniedException")
    void testCreateAssessmentAttributeAiReport_UserDoesNotHaveRequiredPermission_ThrowAccessDeniedException() {
        Long attributeId = 1L;
        UUID currentUserId = UUID.randomUUID();
        var assessment = AssessmentMother.assessment();
        Param param = new Param(assessment.getId(), attributeId, fileLink, currentUserId);

        when(getAssessmentPort.getAssessmentById(param.getAssessmentId())).thenReturn(Optional.of(assessment));
        when(assessmentAccessChecker.isAuthorized(assessment.getId(), param.getCurrentUserId(), EXPORT_ASSESSMENT_REPORT)).thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.createAttributeAiReport(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verify(getAssessmentPort).getAssessmentById(param.getAssessmentId());
        verify(assessmentAccessChecker).isAuthorized(assessment.getId(), currentUserId, EXPORT_ASSESSMENT_REPORT);
        verifyNoInteractions(loadAttributePort,
            loadAssessmentResultPort,
            loadAttributeInsightPort,
            createAssessmentAttributeAiPort,
            updateAttributeInsightPort);
    }

    @Test
    @DisplayName("Create Assessment Attribute AI Report - Assessment Result Not Found - Throw ResourceNotFoundException")
    void testCreateAssessmentAttributeAiReport_AssessmentResultNotFound_ThrowResourceNotFoundException() {
        Long attributeId = 1L;
        UUID currentUserId = UUID.randomUUID();
        var assessment = AssessmentMother.assessment();
        Param param = new Param(assessment.getId(), attributeId, fileLink, currentUserId);

        when(getAssessmentPort.getAssessmentById(param.getAssessmentId())).thenReturn(Optional.of(assessment));
        when(assessmentAccessChecker.isAuthorized(assessment.getId(), param.getCurrentUserId(), EXPORT_ASSESSMENT_REPORT)).thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(assessment.getId())).thenThrow(new ResourceNotFoundException(CREATE_ASSESSMENT_ATTRIBUTE_AI_REPORT_ASSESSMENT_RESULT_NOT_FOUND));

        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.createAttributeAiReport(param));
        assertEquals(CREATE_ASSESSMENT_ATTRIBUTE_AI_REPORT_ASSESSMENT_RESULT_NOT_FOUND, throwable.getMessage());

        verify(getAssessmentPort).getAssessmentById(param.getAssessmentId());
        verify(assessmentAccessChecker).isAuthorized(assessment.getId(), currentUserId, EXPORT_ASSESSMENT_REPORT);
        verifyNoInteractions(loadAttributeInsightPort,
            createAssessmentAttributeAiPort,
            updateAttributeInsightPort);
    }

    @Test
    @DisplayName("Create Assessment Attribute AI Report - Valid Parameters, AI Insight Does Not Exist - Return Text")
    void testCreateAssessmentAttributeAiReport_ValidParametersAiInsightDoesNotExists_ReturnText() {
        UUID currentUserId = UUID.randomUUID();
        var attribute = AttributeMother.simpleAttribute();
        var assessmentResult = AssessmentResultMother.validResultWithJustAnId();
        var assessment = assessmentResult.getAssessment();
        Param param = new Param(assessment.getId(), attribute.getId(), fileLink, currentUserId);
        InputStream downloadFileResult = new ByteArrayInputStream("File Content".getBytes());
        var aiReport = "Report Content";

        when(getAssessmentPort.getAssessmentById(param.getAssessmentId())).thenReturn(Optional.of(assessment));
        when(assessmentAccessChecker.isAuthorized(assessment.getId(), param.getCurrentUserId(), EXPORT_ASSESSMENT_REPORT)).thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(assessment.getId())).thenReturn(Optional.of(assessmentResult));
        when(loadAttributePort.load(attribute.getId(), assessmentResult.getKitVersionId())).thenReturn(attribute);
        when(loadAttributeInsightPort.loadAttributeAiInsight(assessmentResult.getId(), param.getAttributeId())).thenReturn(Optional.empty());
        when(createAssessmentAttributeAiPort.createReport(downloadFileResult, attribute)).thenReturn(aiReport);
        doNothing().when(createAttributeInsightPort).persist(any());
        doReturn(downloadFileResult).when(service).downloadFile(param.getFileLink());

        var result = service.createAttributeAiReport(param);

        assertEquals("Report Content", result.content());
        verifyNoInteractions(updateAttributeInsightPort);
    }

    @Test
    @DisplayName("Create Assessment Attribute AI Report - Calculation Exists, No Insight Needed - Return Text")
    void testCreateAssessmentAttributeAiReport_CalculationExistsNoInsightNeeded_ReturnText() {
        UUID currentUserId = UUID.randomUUID();
        UUID assessmentId = UUID.randomUUID();
        var attribute = AttributeMother.simpleAttribute();
        var assessmentResult = AssessmentResultMother.validResultWithJustAnId();
        var assessment = assessmentResult.getAssessment();
        Param param = new Param(assessmentId, attribute.getId(), fileLink, currentUserId);
        var attributeInsight = AttributeInsightMother.simpleAttributeAiInsight();

        when(getAssessmentPort.getAssessmentById(param.getAssessmentId())).thenReturn(Optional.of(assessment));
        when(assessmentAccessChecker.isAuthorized(assessment.getId(), param.getCurrentUserId(), EXPORT_ASSESSMENT_REPORT)).thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(assessment.getId())).thenReturn(Optional.of(assessmentResult));
        when(loadAttributePort.load(attribute.getId(), assessmentResult.getKitVersionId())).thenReturn(attribute);
        when(loadAttributeInsightPort.loadAttributeAiInsight(assessmentResult.getId(), param.getAttributeId()))
            .thenReturn(Optional.of(attributeInsight));

        var result = assertDoesNotThrow(() -> service.createAttributeAiReport(param));

        assertEquals(result.content(), attributeInsight.getAiInsight());
        verifyNoInteractions(createAssessmentAttributeAiPort, updateAttributeInsightPort);
    }

    @Test
    @DisplayName("Create Assessment Attribute AI Report - Calculation Exists, Insight Needed - Return Text")
    void testCreateAssessmentAttributeAiReport_CalculationExistsInsightNeeded_ReturnText() {
        UUID currentUserId = UUID.randomUUID();
        UUID assessmentId = UUID.randomUUID();
        var attribute = AttributeMother.simpleAttribute();
        var assessmentResult = AssessmentResultMother.validResultWithJustAnId();
        var assessment = assessmentResult.getAssessment();
        Param param = new Param(assessmentId, attribute.getId(), fileLink, currentUserId);
        var attributeInsight = AttributeInsightMother.simpleAttributeAiInsightMinInsightTime();
        InputStream downloadFileResult = new ByteArrayInputStream("File Content".getBytes());

        when(getAssessmentPort.getAssessmentById(param.getAssessmentId())).thenReturn(Optional.of(assessment));
        when(assessmentAccessChecker.isAuthorized(assessment.getId(), param.getCurrentUserId(), EXPORT_ASSESSMENT_REPORT)).thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(assessment.getId())).thenReturn(Optional.of(assessmentResult));
        when(loadAttributePort.load(attribute.getId(), assessmentResult.getKitVersionId())).thenReturn(attribute);
        when(loadAttributeInsightPort.loadAttributeAiInsight(assessmentResult.getId(), param.getAttributeId())).thenReturn(Optional.of(attributeInsight));
        doReturn(downloadFileResult).when(service).downloadFile(param.getFileLink());
        when(createAssessmentAttributeAiPort.createReport(downloadFileResult, attribute)).thenReturn(attributeInsight.getAiInsight());
        doNothing().when(updateAttributeInsightPort).update(any());

        var result = assertDoesNotThrow(() -> service.createAttributeAiReport(param));

        assertEquals(attributeInsight.getAiInsight(), result.content());
        verify(createAssessmentAttributeAiPort).createReport(downloadFileResult, attribute);
        verifyNoInteractions(createAttributeInsightPort);
    }
}
