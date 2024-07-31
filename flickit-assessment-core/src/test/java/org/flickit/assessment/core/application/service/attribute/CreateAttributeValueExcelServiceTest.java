package org.flickit.assessment.core.application.service.attribute;

import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.application.port.out.ValidateAssessmentResultPort;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.core.application.port.in.attribute.CreateAttributeValueExcelUseCase;
import org.flickit.assessment.core.application.port.in.attribute.CreateAttributeValueExcelUseCase.Param;
import org.flickit.assessment.core.application.port.out.attributevalue.GenerateAttributeValueReportFilePort;
import org.flickit.assessment.core.application.port.out.minio.CreateFileDownloadLinkPort;
import org.flickit.assessment.core.application.port.out.minio.UploadAttributeScoreExcelPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.Duration;
import java.util.UUID;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.EXPORT_ASSESSMENT_REPORT;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateAttributeValueExcelServiceTest {


    @InjectMocks
    private CreateAttributeValueExcelService service;

    @Mock
    private AssessmentAccessChecker assessmentAccessChecker;

    @Mock
    private ValidateAssessmentResultPort validateAssessmentResultPort;

    @Mock
    private GenerateAttributeValueReportFilePort generateAttributeValueReportFilePort;

    @Mock
    private UploadAttributeScoreExcelPort uploadAttributeScoreExcelPort;

    @Mock
    private CreateFileDownloadLinkPort createFileDownloadLinkPort;

    @Test
    void testCreateAttributeValueExcel_UserHasNotAccess_ThrowsAccessDenied() {
        Param param = new Param(UUID.randomUUID(),
            15L,
            UUID.randomUUID());

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), EXPORT_ASSESSMENT_REPORT))
            .thenReturn(false);

        assertThrows(AccessDeniedException.class, () -> service.createAttributeValueExcel(param), COMMON_CURRENT_USER_NOT_ALLOWED);

        verifyNoInteractions(validateAssessmentResultPort,
            generateAttributeValueReportFilePort,
            uploadAttributeScoreExcelPort,
            createFileDownloadLinkPort);
    }

    @Test
    void testCreateAttributeValueExcel_ValidParam_uploadExcelAndCreateDownloadLink() {
        Param param = new Param(UUID.randomUUID(),
            15L,
            UUID.randomUUID());
        InputStream inputStream = ByteArrayInputStream.nullInputStream();

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), EXPORT_ASSESSMENT_REPORT))
            .thenReturn(true);
        doNothing().when(validateAssessmentResultPort).validate(param.getAssessmentId());

        when(generateAttributeValueReportFilePort.generateReport(param.getAssessmentId(), param.getAttributeId()))
            .thenReturn(inputStream);

        String filePath = "dir/filename.xlsx";
        String downloadLink = "https://dir/filename.xlsx";
        when(uploadAttributeScoreExcelPort.uploadExcel(any(InputStream.class), any(String.class))).thenReturn(filePath);
        when(createFileDownloadLinkPort.createDownloadLink(eq(filePath), any(Duration.class))).thenReturn(downloadLink);

        CreateAttributeValueExcelUseCase.Result serviceResult = service.createAttributeValueExcel(param);

        assertEquals(downloadLink, serviceResult.downloadLink());
        verify(validateAssessmentResultPort, times(1)).validate(param.getAssessmentId());
    }
}

