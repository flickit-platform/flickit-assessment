package org.flickit.assessment.core.application.service.assessment;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.config.FileProperties;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.common.exception.ValidationException;
import org.flickit.assessment.core.application.port.in.assessment.CreateAssessmentAttributeAiReportUseCase.*;
import org.flickit.assessment.core.application.port.out.aireport.CreateAssessmentAttributeAiPort;
import org.flickit.assessment.core.application.port.out.assessment.GetAssessmentPort;
import org.flickit.assessment.core.application.port.out.minio.DownloadFilePort;
import org.flickit.assessment.core.test.fixture.application.AssessmentMother;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.CREATE_AI_ANALYSIS;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.common.error.ErrorMessageKey.UPLOAD_FILE_FORMAT_NOT_VALID;
import static org.flickit.assessment.core.common.ErrorMessageKey.ASSESSMENT_ID_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateAssessmentAttributeAiReportServiceTest {

    @InjectMocks
    CreateAssessmentAttributeAiReportService service;

    @Mock
    FileProperties fileProperties;

    @Mock
    AssessmentAccessChecker assessmentAccessChecker;

    @Mock
    GetAssessmentPort getAssessmentPort;

    @Mock
    DownloadFilePort downloadFilePort;

    @Mock
    CreateAssessmentAttributeAiPort createAssessmentAttributeAiPort;

    @Test
    @DisplayName("If an assessment with the specified id does not exist, it should produce a NotFound exception.")
    void testCreateAssessmentAttributeAiReport_AssessmentIdDoesNotExist_NotFoundException() {
        UUID assessmentId = UUID.randomUUID();
        UUID currentUserId = UUID.randomUUID();
        String pictureLink = "http://127.0.0.1:9000/avatar/5e3b5d74-cc9c-4b54-b051-86e934ae9a03/temp.xlsx?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-" +
            "Credential=minioadmin%2F20240726%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20240726T052101Z&X-Amz-Expires=604800&X-Amz-SignedHeaders=host&X-Amz-" +
            "Signature=8dfab4d27ab012f1ef15beb58b54da353049f00b9e4a53115eb385b41fb4f4a5";
        Param param = new Param(assessmentId, pictureLink, currentUserId);

        when(getAssessmentPort.getAssessmentById (param.getId())).thenReturn(Optional.empty());

        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.create(param));
        assertEquals(ASSESSMENT_ID_NOT_FOUND, throwable.getMessage());
        verify(getAssessmentPort).getAssessmentById (param.getId());
        verifyNoMoreInteractions(fileProperties, assessmentAccessChecker, downloadFilePort);
    }

    @Test
    @DisplayName("If the current user does not have access to assessment with the specified ID, it should produce an AccessDenied exception")
    void testCreateAssessmentAttributeAiReport_UserDoesNotHaveAccess_AccessDeniedException() {
        UUID assessmentId = UUID.randomUUID();
        UUID currentUserId = UUID.randomUUID();
        String pictureLink = "http://127.0.0.1:9000/avatar/5e3b5d74-cc9c-4b54-b051-86e934ae9a03/temp.xlsx?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-" +
            "Credential=minioadmin%2F20240726%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20240726T052101Z&X-Amz-Expires=604800&X-Amz-SignedHeaders=host&X-Amz-" +
            "Signature=8dfab4d27ab012f1ef15beb58b54da353049f00b9e4a53115eb385b41fb4f4a5";
        Param param = new Param(assessmentId, pictureLink, currentUserId);
        var assessment = AssessmentMother.assessment();

        when(getAssessmentPort.getAssessmentById (param.getId())).thenReturn(Optional.of(assessment));
        when(assessmentAccessChecker.isAuthorized(assessment.getId(), param.getCurrentUserId(), CREATE_AI_ANALYSIS)).thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.create(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());
        verify(assessmentAccessChecker).isAuthorized(assessment.getId(), currentUserId, CREATE_AI_ANALYSIS);
        verifyNoInteractions(fileProperties, downloadFilePort);
    }

    @Test
    @DisplayName("If the file extension is not acceptable, then the AiReportService should produce a Validation exception.")
    void testCreateAssessmentAttributeAiReport_FileExtensionIsNotAcceptable_ValidationException() {
        UUID assessmentId = UUID.randomUUID();
        UUID currentUserId = UUID.randomUUID();
        String pictureLink = "http://127.0.0.1:9000/avatar/5e3b5d74-cc9c-4b54-b051-86e934ae9a03/temp.png?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-" +
            "Credential=minioadmin%2F20240726%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20240726T052101Z&X-Amz-Expires=604800&X-Amz-SignedHeaders=host&X-Amz-" +
            "Signature=8dfab4d27ab012f1ef15beb58b54da353049f00b9e4a53115eb385b41fb4f4a5";
        Param param = new Param(assessmentId, pictureLink, currentUserId);
        var assessment = AssessmentMother.assessment();

        when(getAssessmentPort.getAssessmentById (param.getId())).thenReturn(Optional.of(assessment));
        when(assessmentAccessChecker.isAuthorized(assessment.getId(), param.getCurrentUserId(), CREATE_AI_ANALYSIS)).thenReturn(true);
        when(fileProperties.getAttributeReportFileExtension()).thenReturn(List.of("xlsx"));

        var throwable = assertThrows(ValidationException.class, () -> service.create(param));
        assertEquals(UPLOAD_FILE_FORMAT_NOT_VALID, throwable.getMessageKey());
        verify(fileProperties).getAttributeReportFileExtension();
        verify(getAssessmentPort).getAssessmentById (param.getId());
        verify(assessmentAccessChecker).isAuthorized(assessment.getId(), param.getCurrentUserId(), CREATE_AI_ANALYSIS);
        verifyNoInteractions(downloadFilePort, createAssessmentAttributeAiPort);
    }

    @Test
    @DisplayName("If the parameters are valid, the service should return a valid report.")
    void testCreateAssessmentAttributeAiReport_ValidParameters_ReturnText() throws Exception {
        UUID assessmentId = UUID.randomUUID();
        UUID currentUserId = UUID.randomUUID();
        String pictureLink = "http://127.0.0.1:9000/avatar/5e3b5d74-cc9c-4b54-b051-86e934ae9a03/temp.xlsx?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-" +
            "Credential=minioadmin%2F20240726%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20240726T052101Z&X-Amz-Expires=604800&X-Amz-SignedHeaders=host&X-Amz-" +
            "Signature=8dfab4d27ab012f1ef15beb58b54da353049f00b9e4a53115eb385b41fb4f4a5";
        Param param = new Param(assessmentId, pictureLink, currentUserId);
        var assessment = AssessmentMother.assessment();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (Workbook workbook = new XSSFWorkbook()) {
            workbook.createSheet("Software Security");
            workbook.write(baos);
        }
        InputStream mockInputStream = new ByteArrayInputStream(baos.toByteArray());

        when(getAssessmentPort.getAssessmentById (param.getId())).thenReturn(Optional.of(assessment));
        when(fileProperties.getAttributeReportFileExtension()).thenReturn(List.of("xlsx"));
        when(assessmentAccessChecker.isAuthorized(assessment.getId(), param.getCurrentUserId(), CREATE_AI_ANALYSIS)).thenReturn(true);
        when(downloadFilePort.downloadFile(param.getFileLink())).thenReturn(mockInputStream);

        assertDoesNotThrow(() -> service.create(param));
        verify(getAssessmentPort).getAssessmentById (param.getId());
        verify(fileProperties).getAttributeReportFileExtension();
        verify(assessmentAccessChecker).isAuthorized(assessment.getId(), currentUserId, CREATE_AI_ANALYSIS);
        verify(downloadFilePort).downloadFile(param.getFileLink());
        verify(createAssessmentAttributeAiPort).createReport(mockInputStream, "Software Security");
    }

}
