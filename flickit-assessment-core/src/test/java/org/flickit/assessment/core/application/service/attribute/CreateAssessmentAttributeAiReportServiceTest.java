package org.flickit.assessment.core.application.service.attribute;

import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.config.FileProperties;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.common.exception.ValidationException;
import org.flickit.assessment.core.application.port.in.attribute.CreateAssessmentAttributeAiReportUseCase.Param;
import org.flickit.assessment.core.application.port.out.assessment.GetAssessmentPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.core.application.port.out.attribute.CreateAssessmentAttributeAiPort;
import org.flickit.assessment.core.application.port.out.attribute.LoadAttributePort;
import org.flickit.assessment.core.test.fixture.application.AssessmentMother;
import org.flickit.assessment.core.test.fixture.application.AssessmentResultMother;
import org.flickit.assessment.core.test.fixture.application.AttributeMother;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.EXPORT_ASSESSMENT_REPORT;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.common.error.ErrorMessageKey.UPLOAD_FILE_FORMAT_NOT_VALID;
import static org.flickit.assessment.core.common.ErrorMessageKey.ASSESSMENT_ID_NOT_FOUND;
import static org.flickit.assessment.core.common.ErrorMessageKey.CREATE_ASSESSMENT_ATTRIBUTE_AI_REPORT_ASSESSMENT_RESULT_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateAssessmentAttributeAiReportServiceTest {

    CreateAssessmentAttributeAiReportService service;

    @Mock
    GetAssessmentPort getAssessmentPort;

    @Mock
    AssessmentAccessChecker assessmentAccessChecker;

    @Mock
    LoadAssessmentResultPort loadAssessmentResultPort;

    @Mock
    CreateAssessmentAttributeAiPort createAssessmentAttributeAiPort;

    @Mock
    LoadAttributePort loadAttributePort;

    final String fileLink = fileLink("xlsx");
    final String fileLinkWithInvalidExtension = fileLink("png");

    @BeforeEach
    void prepare() {
        var fileProperties = new FileProperties();
        fileProperties.setAttributeReportFileExtension(List.of("xlsx"));
        service = spy(new CreateAssessmentAttributeAiReportService(fileProperties, loadAttributePort, getAssessmentPort,
            assessmentAccessChecker, loadAssessmentResultPort, createAssessmentAttributeAiPort));
    }

    @Test
    void testCreateAssessmentAttributeAiReport_AssessmentNotFound_ThrowResourceNotFoundException() {
        UUID assessmentId = UUID.randomUUID();
        Long attributeId = 1L;
        UUID currentUserId = UUID.randomUUID();
        Param param = new Param(assessmentId, attributeId, fileLink, currentUserId);

        when(getAssessmentPort.getAssessmentById(param.getAssessmentId())).thenReturn(Optional.empty());

        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.createAttributeAiReport(param));
        assertEquals(ASSESSMENT_ID_NOT_FOUND, throwable.getMessage());

        verify(getAssessmentPort).getAssessmentById(param.getAssessmentId());
        verifyNoMoreInteractions(assessmentAccessChecker);
    }

    @Test
    void testCreateAssessmentAttributeAiReport_UserDoesNotHaveRequiredPermission_ThrowAccessDeniedException() {
        Long attributeId = 1L;
        UUID currentUserId = UUID.randomUUID();
        var assessment = AssessmentMother.assessment();
        Param param = new Param(assessment.getId(), attributeId, fileLink, currentUserId);

        when(getAssessmentPort.getAssessmentById(param.getAssessmentId())).thenReturn(Optional.of(assessment));
        when(assessmentAccessChecker.isAuthorized(assessment.getId(), param.getCurrentUserId(), EXPORT_ASSESSMENT_REPORT)).thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.createAttributeAiReport(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verify(assessmentAccessChecker).isAuthorized(assessment.getId(), currentUserId, EXPORT_ASSESSMENT_REPORT);
    }

    @Test
    void testCreateAssessmentAttributeAiReport_FileExtensionIsNotAcceptable_ThrowValidationException() {
        Long attributeId = 1L;
        UUID currentUserId = UUID.randomUUID();
        var assessment = AssessmentMother.assessment();
        Param param = new Param(assessment.getId(), attributeId, fileLinkWithInvalidExtension, currentUserId);

        when(getAssessmentPort.getAssessmentById(param.getAssessmentId())).thenReturn(Optional.of(assessment));
        when(assessmentAccessChecker.isAuthorized(assessment.getId(), param.getCurrentUserId(), EXPORT_ASSESSMENT_REPORT)).thenReturn(true);

        var throwable = assertThrows(ValidationException.class, () -> service.createAttributeAiReport(param));
        assertEquals(UPLOAD_FILE_FORMAT_NOT_VALID, throwable.getMessageKey());

        verify(getAssessmentPort).getAssessmentById(param.getAssessmentId());
        verify(assessmentAccessChecker).isAuthorized(assessment.getId(), param.getCurrentUserId(), EXPORT_ASSESSMENT_REPORT);
        verifyNoInteractions(loadAssessmentResultPort, createAssessmentAttributeAiPort);
    }

    @Test
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
        verifyNoInteractions(createAssessmentAttributeAiPort);
    }

    @Test
    void testCreateAssessmentAttributeAiReport_ValidParameters_ReturnText() {
        UUID currentUserId = UUID.randomUUID();
        var attribute = AttributeMother.simpleAttribute();
        var assessmentResult = AssessmentResultMother.validResultWithJustAnId();
        var assessment = assessmentResult.getAssessment();
        Param param = new Param(assessment.getId(), attribute.getId(), fileLink, currentUserId);

        when(getAssessmentPort.getAssessmentById(param.getAssessmentId())).thenReturn(Optional.of(assessment));
        when(assessmentAccessChecker.isAuthorized(assessment.getId(), param.getCurrentUserId(), EXPORT_ASSESSMENT_REPORT)).thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(assessment.getId())).thenReturn(Optional.of(assessmentResult));
        when(loadAttributePort.load(attribute.getId(), assessmentResult.getKitVersionId())).thenReturn(attribute);

        InputStream downloadFileResult = new ByteArrayInputStream("File Content".getBytes());
        doReturn(downloadFileResult).when(service).downloadFile(param.getFileLink());
        when(createAssessmentAttributeAiPort.createReport(downloadFileResult, attribute)).thenReturn("Report Content");

        var result = service.createAttributeAiReport(param);

        assertEquals("Report Content", result.content());
    }

    private static String fileLink(String fileExtension) {
        return "http://127.0.0.1:9000/report/5e3b5d74-cc9c-4b54-b051-86e934ae9a03/temp." + fileExtension +
            "?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-" +
            "Credential=minioadmin%2F20240726%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20240726T052101Z&X-Amz-Expires=604800&X-Amz-SignedHeaders=host&X-Amz-" +
            "Signature=8dfab4d27ab012f1ef15beb58b54da353049f00b9e4a53115eb385b41fb4f4a5";
    }
}
