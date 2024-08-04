package org.flickit.assessment.core.application.service.attribute;

import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.application.port.out.ValidateAssessmentResultPort;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.core.application.domain.AssessmentResult;
import org.flickit.assessment.core.application.domain.AttributeValue;
import org.flickit.assessment.core.application.domain.MaturityLevel;
import org.flickit.assessment.core.application.port.in.attribute.CreateAttributeValueReportFileUseCase;
import org.flickit.assessment.core.application.port.in.attribute.CreateAttributeValueReportFileUseCase.Param;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.core.application.port.out.attributevalue.GenerateAttributeValueReportFilePort;
import org.flickit.assessment.core.application.port.out.attributevalue.LoadAttributeValuePort;
import org.flickit.assessment.core.application.port.out.maturitylevel.LoadMaturityLevelsPort;
import org.flickit.assessment.core.application.port.out.minio.CreateFileDownloadLinkPort;
import org.flickit.assessment.core.application.port.out.minio.UploadAttributeScoreExcelPort;
import org.flickit.assessment.core.test.fixture.application.AssessmentResultMother;
import org.flickit.assessment.core.test.fixture.application.AttributeValueMother;
import org.flickit.assessment.core.test.fixture.application.MaturityLevelMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.EXPORT_ASSESSMENT_REPORT;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateAttributeValueReportFileServiceTest {

    @InjectMocks
    private CreateAttributeValueReportFileService service;

    @Mock
    private AssessmentAccessChecker assessmentAccessChecker;

    @Mock
    private ValidateAssessmentResultPort validateAssessmentResultPort;

    @Mock
    private LoadAssessmentResultPort loadAssessmentResultPort;

    @Mock
    private LoadAttributeValuePort loadAttributeValuePort;

    @Mock
    private LoadMaturityLevelsPort loadMaturityLevelsPort;

    @Mock
    private GenerateAttributeValueReportFilePort generateAttributeValueReportFilePort;

    @Mock
    private UploadAttributeScoreExcelPort uploadAttributeScoreExcelPort;

    @Mock
    private CreateFileDownloadLinkPort createFileDownloadLinkPort;

    @Test
    void testCreateAttributeValueReportFile_UserHasNotAccess_ThrowsAccessDenied() {
        Param param = new Param(UUID.randomUUID(),
            15L,
            UUID.randomUUID());

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), EXPORT_ASSESSMENT_REPORT))
            .thenReturn(false);

        assertThrows(AccessDeniedException.class, () -> service.createAttributeValueReportFile(param), COMMON_CURRENT_USER_NOT_ALLOWED);

        verifyNoInteractions(validateAssessmentResultPort,
            loadMaturityLevelsPort,
            generateAttributeValueReportFilePort,
            uploadAttributeScoreExcelPort,
            createFileDownloadLinkPort);
    }

    @Test
    void testCreateAttributeValueReportFile_ValidParam_uploadExcelAndCreateDownloadLink() {
        Param param = new Param(UUID.randomUUID(),
            15L,
            UUID.randomUUID());
        AssessmentResult assessmentResult = AssessmentResultMother.validResultWithJustAnId();
        AttributeValue attributeValue = AttributeValueMother.toBeCalcAsLevelThreeWithWeight(1);
        List<MaturityLevel> maturityLevels = MaturityLevelMother.allLevels();
        InputStream inputStream = ByteArrayInputStream.nullInputStream();

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), EXPORT_ASSESSMENT_REPORT))
            .thenReturn(true);
        doNothing().when(validateAssessmentResultPort).validate(param.getAssessmentId());

        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        when(loadAttributeValuePort.load(assessmentResult.getId(), param.getAttributeId())).thenReturn(attributeValue);
        when(loadMaturityLevelsPort.loadByKitVersionId(assessmentResult.getKitVersionId())).thenReturn(maturityLevels);

        when(generateAttributeValueReportFilePort.generateReport(attributeValue, maturityLevels))
            .thenReturn(inputStream);

        String filePath = "dir/filename.xlsx";
        String downloadLink = "https://dir/filename.xlsx";
        when(uploadAttributeScoreExcelPort.uploadExcel(any(InputStream.class), any(String.class))).thenReturn(filePath);
        when(createFileDownloadLinkPort.createDownloadLink(eq(filePath), any(Duration.class))).thenReturn(downloadLink);

        CreateAttributeValueReportFileUseCase.Result serviceResult = service.createAttributeValueReportFile(param);

        assertEquals(downloadLink, serviceResult.downloadLink());
        verify(validateAssessmentResultPort, times(1)).validate(param.getAssessmentId());
    }
}

