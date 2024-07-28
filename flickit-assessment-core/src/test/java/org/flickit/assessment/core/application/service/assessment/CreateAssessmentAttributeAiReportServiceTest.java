package org.flickit.assessment.core.application.service.assessment;

import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.config.FileProperties;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.common.exception.ValidationException;
import org.flickit.assessment.core.application.port.in.attribute.CreateAssessmentAttributeAiReportUseCase.*;
import org.flickit.assessment.core.application.port.out.attribute.CreateAssessmentAttributeAiPort;
import org.flickit.assessment.core.application.port.out.assessment.GetAssessmentPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.core.application.port.out.attribute.LoadAttributePort;
import org.flickit.assessment.core.application.port.out.minio.DownloadFilePort;
import org.flickit.assessment.core.application.service.attribute.CreateAssessmentAttributeAiReportService;
import org.flickit.assessment.core.test.fixture.application.AssessmentMother;
import org.flickit.assessment.core.test.fixture.application.AssessmentResultMother;
import org.flickit.assessment.core.test.fixture.application.AttributeMother;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.CREATE_AI_ANALYSIS;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.common.error.ErrorMessageKey.UPLOAD_FILE_FORMAT_NOT_VALID;
import static org.flickit.assessment.core.common.ErrorMessageKey.ASSESSMENT_ID_NOT_FOUND;
import static org.flickit.assessment.core.common.ErrorMessageKey.CREATE_ASSESSMENT_ATTRIBUTE_AI_REPORT_ASSESSMENT_RESULT_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateAssessmentAttributeAiReportServiceTest {

    @InjectMocks
    CreateAssessmentAttributeAiReportService service;

    @Mock
    GetAssessmentPort getAssessmentPort;

    @Mock
    AssessmentAccessChecker assessmentAccessChecker;

    @Mock
    FileProperties fileProperties;

    @Mock
    LoadAssessmentResultPort loadAssessmentResultPort;

    @Mock
    DownloadFilePort downloadFilePort;

    @Mock
    CreateAssessmentAttributeAiPort createAssessmentAttributeAiPort;

    @Mock
    LoadAttributePort loadAttributePort;

    @Test
    @DisplayName("If an assessment with the specified id does not exist, it should produce a NotFound exception.")
    void testCreateAssessmentAttributeAiReport_AssessmentIdDoesNotExist_NotFoundException() {
        UUID assessmentId = UUID.randomUUID();
        Long attributeId = 1L;
        UUID currentUserId = UUID.randomUUID();
        String pictureLink = "http://127.0.0.1:9000/avatar/5e3b5d74-cc9c-4b54-b051-86e934ae9a03/temp.xlsx?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-" +
            "Credential=minioadmin%2F20240726%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20240726T052101Z&X-Amz-Expires=604800&X-Amz-SignedHeaders=host&X-Amz-" +
            "Signature=8dfab4d27ab012f1ef15beb58b54da353049f00b9e4a53115eb385b41fb4f4a5";
        Param param = new Param(assessmentId, attributeId, pictureLink, currentUserId);

        when(getAssessmentPort.getAssessmentById (param.getAssessmentId())).thenReturn(Optional.empty());

        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.create(param));
        assertEquals(ASSESSMENT_ID_NOT_FOUND, throwable.getMessage());
        verify(getAssessmentPort).getAssessmentById (param.getAssessmentId());
        verifyNoMoreInteractions(fileProperties, assessmentAccessChecker, downloadFilePort);
    }

    @Test
    @DisplayName("If the current user does not have access to assessment with the specified ID, it should produce an AccessDenied exception")
    void testCreateAssessmentAttributeAiReport_UserDoesNotHaveAccess_AccessDeniedException() {
        UUID assessmentId = UUID.randomUUID();
        Long attributeId = 1L;
        UUID currentUserId = UUID.randomUUID();
        String pictureLink = "http://127.0.0.1:9000/avatar/5e3b5d74-cc9c-4b54-b051-86e934ae9a03/temp.xlsx?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-" +
            "Credential=minioadmin%2F20240726%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20240726T052101Z&X-Amz-Expires=604800&X-Amz-SignedHeaders=host&X-Amz-" +
            "Signature=8dfab4d27ab012f1ef15beb58b54da353049f00b9e4a53115eb385b41fb4f4a5";
        Param param = new Param(assessmentId, attributeId, pictureLink, currentUserId);
        var assessment = AssessmentMother.assessment();

        when(getAssessmentPort.getAssessmentById (param.getAssessmentId())).thenReturn(Optional.of(assessment));
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
        Long attributeId = 1L;
        UUID currentUserId = UUID.randomUUID();
        String pictureLink = "http://127.0.0.1:9000/avatar/5e3b5d74-cc9c-4b54-b051-86e934ae9a03/temp.png?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-" +
            "Credential=minioadmin%2F20240726%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20240726T052101Z&X-Amz-Expires=604800&X-Amz-SignedHeaders=host&X-Amz-" +
            "Signature=8dfab4d27ab012f1ef15beb58b54da353049f00b9e4a53115eb385b41fb4f4a5";
        Param param = new Param(assessmentId, attributeId, pictureLink, currentUserId);
        var assessment = AssessmentMother.assessment();

        when(getAssessmentPort.getAssessmentById (param.getAssessmentId())).thenReturn(Optional.of(assessment));
        when(assessmentAccessChecker.isAuthorized(assessment.getId(), param.getCurrentUserId(), CREATE_AI_ANALYSIS)).thenReturn(true);
        when(fileProperties.getAttributeReportFileExtension()).thenReturn(List.of("xlsx"));

        var throwable = assertThrows(ValidationException.class, () -> service.create(param));
        assertEquals(UPLOAD_FILE_FORMAT_NOT_VALID, throwable.getMessageKey());
        verify(fileProperties).getAttributeReportFileExtension();
        verify(getAssessmentPort).getAssessmentById (param.getAssessmentId());
        verify(assessmentAccessChecker).isAuthorized(assessment.getId(), param.getCurrentUserId(), CREATE_AI_ANALYSIS);
        verifyNoInteractions(downloadFilePort, loadAssessmentResultPort, createAssessmentAttributeAiPort);
    }

    @Test
    @DisplayName("If an assessmentResult for the the specified assessment does not exist, the CreateAssessmentAttribute service should throw NotFoundException.")
    void testCreateAssessmentAttributeAiReport_AssessmentResultNotFound_ReturnText() {
        UUID assessmentId = UUID.randomUUID();
        Long attributeId = 1L;
        UUID currentUserId = UUID.randomUUID();
        String pictureLink = "http://127.0.0.1:9000/avatar/5e3b5d74-cc9c-4b54-b051-86e934ae9a03/temp.xlsx?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-" +
            "Credential=minioadmin%2F20240726%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20240726T052101Z&X-Amz-Expires=604800&X-Amz-SignedHeaders=host&X-Amz-" +
            "Signature=8dfab4d27ab012f1ef15beb58b54da353049f00b9e4a53115eb385b41fb4f4a5";
        Param param = new Param(assessmentId, attributeId, pictureLink, currentUserId);
        var assessment = AssessmentMother.assessment();

        when(getAssessmentPort.getAssessmentById (param.getAssessmentId())).thenReturn(Optional.of(assessment));
        when(assessmentAccessChecker.isAuthorized(assessment.getId(), param.getCurrentUserId(), CREATE_AI_ANALYSIS)).thenReturn(true);
        when(fileProperties.getAttributeReportFileExtension()).thenReturn(List.of("xlsx"));
        when(loadAssessmentResultPort.loadByAssessmentId(assessment.getId())).thenThrow(new ResourceNotFoundException(CREATE_ASSESSMENT_ATTRIBUTE_AI_REPORT_ASSESSMENT_RESULT_NOT_FOUND));

        var throwable = assertThrows(ResourceNotFoundException.class, ()->service.create(param));
        assertEquals(CREATE_ASSESSMENT_ATTRIBUTE_AI_REPORT_ASSESSMENT_RESULT_NOT_FOUND, throwable.getMessage());

        verify(getAssessmentPort).getAssessmentById (param.getAssessmentId());
        verify(assessmentAccessChecker).isAuthorized(assessment.getId(), currentUserId, CREATE_AI_ANALYSIS);
        verifyNoInteractions(downloadFilePort, createAssessmentAttributeAiPort);
    }

    @Test
    @DisplayName("If the parameters are valid, the service should return a valid report.")
    void testCreateAssessmentAttributeAiReport_ValidParameters_ReturnText() throws Exception {
        UUID assessmentId = UUID.randomUUID();
        Long attributeId = 1L;
        UUID currentUserId = UUID.randomUUID();
        String pictureLink = "http://127.0.0.1:9000/avatar/5e3b5d74-cc9c-4b54-b051-86e934ae9a03/temp.xlsx?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-" +
            "Credential=minioadmin%2F20240726%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20240726T052101Z&X-Amz-Expires=604800&X-Amz-SignedHeaders=host&X-Amz-" +
            "Signature=8dfab4d27ab012f1ef15beb58b54da353049f00b9e4a53115eb385b41fb4f4a5";
        Param param = new Param(assessmentId, attributeId, pictureLink, currentUserId);
        var assessment = AssessmentMother.assessment();
        var attribute = AttributeMother.completeAttribute(1L,null, 0);
        var assessmentResult = AssessmentResultMother.validResultWithJustAnId();


        InputStream mockInputStream = new ByteArrayInputStream("sample.xlsx".getBytes());

        when(getAssessmentPort.getAssessmentById (param.getAssessmentId())).thenReturn(Optional.of(assessment));
        when(assessmentAccessChecker.isAuthorized(assessment.getId(), param.getCurrentUserId(), CREATE_AI_ANALYSIS)).thenReturn(true);
        when(fileProperties.getAttributeReportFileExtension()).thenReturn(List.of("xlsx"));
        when(loadAssessmentResultPort.loadByAssessmentId(assessment.getId())).thenReturn(Optional.of((assessmentResult)));
        when(downloadFilePort.downloadFile(param.getFileLink())).thenReturn(mockInputStream);
        when(loadAttributePort.load(anyLong(), anyLong())).thenReturn(attribute);
        when(createAssessmentAttributeAiPort.createReport(mockInputStream, attribute)).thenReturn("Some String");

        assertDoesNotThrow(() -> service.create(param));
        verify(getAssessmentPort).getAssessmentById (param.getAssessmentId());
        verify(fileProperties).getAttributeReportFileExtension();
        verify(assessmentAccessChecker).isAuthorized(assessment.getId(), currentUserId, CREATE_AI_ANALYSIS);
        verify(downloadFilePort).downloadFile(param.getFileLink());
        verify(createAssessmentAttributeAiPort).createReport(mockInputStream, attribute);
        verify(loadAttributePort).load(attribute.getId(), assessmentResult.getKitVersionId());
    }
}
