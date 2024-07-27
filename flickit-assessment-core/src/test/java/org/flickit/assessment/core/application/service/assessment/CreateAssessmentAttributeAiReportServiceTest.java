package org.flickit.assessment.core.application.service.assessment;

import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.config.FileProperties;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ValidationException;
import org.flickit.assessment.core.application.port.in.assessment.CreateAssessmentAttributeAiReportUseCase.*;
import org.flickit.assessment.core.application.port.out.aireport.CreateAssessmentAttributeAiPort;
import org.flickit.assessment.core.application.port.out.minio.DownloadFilePort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.CREATE_AI_ANALYSIS;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.common.error.ErrorMessageKey.UPLOAD_FILE_FORMAT_NOT_VALID;
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
    DownloadFilePort downloadFilePort;

    @Mock
    CreateAssessmentAttributeAiPort createAssessmentAttributeAiPort;

    @Test
    @DisplayName("if file extension is not acceptable, then the AiReportService should produce a Validation exception.")
    void testCreateAssessmentAttributeAiReport_FileExtensionIsNotAcceptable_ValidationException() {
        UUID assessmentId = UUID.randomUUID();
        UUID currentUserId = UUID.randomUUID();
        String pictureLink = "http://127.0.0.1:9000/avatar/5e3b5d74-cc9c-4b54-b051-86e934ae9a03/temp.png?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-" +
            "Credential=minioadmin%2F20240726%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20240726T052101Z&X-Amz-Expires=604800&X-Amz-SignedHeaders=host&X-Amz-" +
            "Signature=8dfab4d27ab012f1ef15beb58b54da353049f00b9e4a53115eb385b41fb4f4a5";
        Param param = new Param(assessmentId, pictureLink, currentUserId);

        when(fileProperties.getAttributeReportFileExtension()).thenReturn(List.of("xlsx"));

        var throwable = assertThrows(ValidationException.class, () -> service.create(param));
        assertEquals(UPLOAD_FILE_FORMAT_NOT_VALID, throwable.getMessageKey());
        verify(fileProperties).getAttributeReportFileExtension();
        verifyNoInteractions(assessmentAccessChecker, createAssessmentAttributeAiPort);
    }

    @Test
    @DisplayName("if an assessment with the specified id does not exists, it should produce an NotFound exception")
    void testCreateAssessmentAttributeAiReport_AssessmentIdDoesNotExist_NotFoundException() {
        UUID assessmentId = UUID.randomUUID();
        UUID currentUserId = UUID.randomUUID();
        String pictureLink = "http://127.0.0.1:9000/avatar/5e3b5d74-cc9c-4b54-b051-86e934ae9a03/temp.xlsx?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-" +
            "Credential=minioadmin%2F20240726%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20240726T052101Z&X-Amz-Expires=604800&X-Amz-SignedHeaders=host&X-Amz-" +
            "Signature=8dfab4d27ab012f1ef15beb58b54da353049f00b9e4a53115eb385b41fb4f4a5";
        Param param = new Param(assessmentId, pictureLink, currentUserId);

        when(fileProperties.getAttributeReportFileExtension()).thenReturn(List.of("xlsx"));
        when(assessmentAccessChecker.isAuthorized(param.getId(), param.getCurrentUserId(), CREATE_AI_ANALYSIS)).thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.create(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());
        verify(fileProperties).getAttributeReportFileExtension();
        verify(assessmentAccessChecker).isAuthorized(assessmentId, currentUserId, CREATE_AI_ANALYSIS);
    }

    @Test
    @DisplayName("if the parameters are valid, it should produce an NotFound exception")
    void testCreateAssessmentAttributeAiReport_ValidParameters_ReturnText() {
        UUID assessmentId = UUID.randomUUID();
        UUID currentUserId = UUID.randomUUID();
        String pictureLink = "http://127.0.0.1:9000/avatar/5e3b5d74-cc9c-4b54-b051-86e934ae9a03/temp.xlsx?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-" +
            "Credential=minioadmin%2F20240726%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20240726T052101Z&X-Amz-Expires=604800&X-Amz-SignedHeaders=host&X-Amz-" +
            "Signature=8dfab4d27ab012f1ef15beb58b54da353049f00b9e4a53115eb385b41fb4f4a5";
        Param param = new Param(assessmentId, pictureLink, currentUserId);
        String fileContent = "dummy file content";
        InputStream mockInputStream = new ByteArrayInputStream(fileContent.getBytes());

        when(fileProperties.getAttributeReportFileExtension()).thenReturn(List.of("xlsx"));
        when(assessmentAccessChecker.isAuthorized(param.getId(), param.getCurrentUserId(), CREATE_AI_ANALYSIS)).thenReturn(true);
        when(downloadFilePort.downloadFile(param.getFileLink())).thenReturn(mockInputStream);

        assertDoesNotThrow(() -> service.create(param));
        verify(fileProperties).getAttributeReportFileExtension();
        verify(assessmentAccessChecker).isAuthorized(assessmentId, currentUserId, CREATE_AI_ANALYSIS);
        verify(downloadFilePort).downloadFile(param.getFileLink());
        verify(createAssessmentAttributeAiPort).createReport(any());
    }

}
