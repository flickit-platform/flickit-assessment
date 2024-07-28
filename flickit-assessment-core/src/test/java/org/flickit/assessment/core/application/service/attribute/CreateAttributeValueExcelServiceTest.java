package org.flickit.assessment.core.application.service.attribute;

import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.application.port.out.ValidateAssessmentResultPort;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.core.application.domain.*;
import org.flickit.assessment.core.application.port.in.attribute.CreateAttributeValueExcelUseCase;
import org.flickit.assessment.core.application.port.in.attribute.CreateAttributeValueExcelUseCase.Param;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadCalculateInfoPort;
import org.flickit.assessment.core.application.port.out.attributevalue.LoadAttributeValuePort;
import org.flickit.assessment.core.application.port.out.maturitylevel.LoadMaturityLevelsPort;
import org.flickit.assessment.core.application.port.out.minio.CreateFileDownloadLinkPort;
import org.flickit.assessment.core.application.port.out.minio.UploadAttributeScoreExcelPort;
import org.flickit.assessment.core.test.fixture.application.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.InputStream;
import java.time.Duration;
import java.util.List;
import java.util.UUID;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.VIEW_ATTRIBUTE_VALUE_EXCEL;
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
    private LoadCalculateInfoPort loadCalculateInfoPort;

    @Mock
    private LoadAttributeValuePort loadAttributeValuePort;

    @Mock
    private LoadMaturityLevelsPort loadMaturityLevelsPort;

    @Mock
    private UploadAttributeScoreExcelPort uploadAttributeScoreExcelPort;

    @Mock
    private CreateFileDownloadLinkPort createFileDownloadLinkPort;

    @Test
    void testCreateAttributeValueExcel_UserHasNotAccess_ThrowsAccessDenied() {
        Param param = new Param(UUID.randomUUID(),
            15L,
            UUID.randomUUID());

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ATTRIBUTE_VALUE_EXCEL))
            .thenReturn(false);

        assertThrows(AccessDeniedException.class, () -> service.createAttributeValueExcel(param), COMMON_CURRENT_USER_NOT_ALLOWED);

        verifyNoInteractions(validateAssessmentResultPort,
            loadCalculateInfoPort,
            loadAttributeValuePort,
            loadMaturityLevelsPort,
            uploadAttributeScoreExcelPort,
            createFileDownloadLinkPort);
    }

    @Test
    void testCreateAttributeValueExcel_ValidParam_uploadExcelAndCreateDownloadLink() {
        Param param = new Param(UUID.randomUUID(),
            15L,
            UUID.randomUUID());
        Answer answer = AnswerMother.fullScoreOnLevels23(param.getAttributeId());
        Question question = QuestionMother.withIdAndImpactsOnLevel23(answer.getQuestionId(), param.getAttributeId());
        Attribute attribute = AttributeMother.withIdAndQuestions(param.getAttributeId(), List.of(question));
        AttributeValue attributeValue = AttributeValueMother.withAttributeAndAnswerAndLevelOne(attribute, List.of(answer));

        AssessmentResult assessmentResult = AssessmentResultMother.validResultWithSubjectValuesAndMaturityLevel(
            List.of(SubjectValueMother.withQAValues(List.of(attributeValue))),
            MaturityLevelMother.levelOne()
        );

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ATTRIBUTE_VALUE_EXCEL))
            .thenReturn(true);
        doNothing().when(validateAssessmentResultPort).validate(param.getAssessmentId());

        when(loadCalculateInfoPort.load(param.getAssessmentId())).thenReturn(assessmentResult);
        when(loadAttributeValuePort.load(attributeValue.getId())).thenReturn(attributeValue);
        when(loadMaturityLevelsPort.loadByKitVersionId(assessmentResult.getKitVersionId())).thenReturn(MaturityLevelMother.allLevels());

        String filePath = "dir/filename.xlsx";
        String downloadLink = "https://dir/filename.xlsx";
        when(uploadAttributeScoreExcelPort.uploadExcel(any(InputStream.class), eq(attribute.getTitle()))).thenReturn(filePath);
        when(createFileDownloadLinkPort.createDownloadLink(eq(filePath), any(Duration.class))).thenReturn(downloadLink);

        CreateAttributeValueExcelUseCase.Result serviceResult = service.createAttributeValueExcel(param);

        assertEquals(downloadLink, serviceResult.downloadLink());
        verify(validateAssessmentResultPort, times(1)).validate(param.getAssessmentId());
    }
}

