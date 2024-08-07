package org.flickit.assessment.core.application.service.attribute;

import org.flickit.assessment.common.application.MessageBundle;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.application.port.out.ValidateAssessmentResultPort;
import org.flickit.assessment.common.config.OpenAiProperties;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.CalculateNotValidException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.AttributeValue;
import org.flickit.assessment.core.application.domain.MaturityLevel;
import org.flickit.assessment.core.application.port.in.attribute.CreateAssessmentAttributeAiReportUseCase.Param;
import org.flickit.assessment.core.application.port.out.assessment.GetAssessmentPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.core.application.port.out.attribute.CreateAttributeAiInsightPort;
import org.flickit.assessment.core.application.port.out.attribute.LoadAttributePort;
import org.flickit.assessment.core.application.port.out.attributeinsight.CreateAttributeInsightPort;
import org.flickit.assessment.core.application.port.out.attributeinsight.LoadAttributeInsightPort;
import org.flickit.assessment.core.application.port.out.attributeinsight.UpdateAttributeInsightPort;
import org.flickit.assessment.core.application.port.out.attribute.CreateAttributeScoresFilePort;
import org.flickit.assessment.core.application.port.out.attributevalue.LoadAttributeValuePort;
import org.flickit.assessment.core.application.port.out.maturitylevel.LoadMaturityLevelsPort;
import org.flickit.assessment.core.test.fixture.application.*;
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

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.EXPORT_ASSESSMENT_REPORT;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_ASSESSMENT_RESULT_NOT_VALID;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.core.common.ErrorMessageKey.ASSESSMENT_ID_NOT_FOUND;
import static org.flickit.assessment.core.common.ErrorMessageKey.CREATE_ASSESSMENT_ATTRIBUTE_AI_REPORT_ASSESSMENT_RESULT_NOT_FOUND;
import static org.flickit.assessment.core.common.MessageKey.ASSESSMENT_ATTRIBUTE_AI_IS_DISABLED;
import static org.flickit.assessment.core.test.fixture.application.AttributeInsightMother.simpleAttributeAiInsightMinInsightTime;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateAssessmentAttributeAiReportServiceTest {

    @InjectMocks
    private CreateAssessmentAttributeAiReportService service;

    @Mock
    private OpenAiProperties openAiProperties;

    @Mock
    private ValidateAssessmentResultPort validateAssessmentResultPort;

    @Mock
    private GetAssessmentPort getAssessmentPort;

    @Mock
    private LoadAttributeValuePort loadAttributeValuePort;

    @Mock
    private LoadMaturityLevelsPort loadMaturityLevelsPort;

    @Mock
    private AssessmentAccessChecker assessmentAccessChecker;

    @Mock
    private LoadAssessmentResultPort loadAssessmentResultPort;

    @Mock
    private CreateAttributeAiInsightPort createAttributeAiInsightPort;

    @Mock
    private UpdateAttributeInsightPort updateAttributeInsightPort;

    @Mock
    private LoadAttributePort loadAttributePort;

    @Mock
    private LoadAttributeInsightPort loadAttributeInsightPort;

    @Mock
    private CreateAttributeInsightPort createAttributeInsightPort;

    @Mock
    CreateAttributeScoresFilePort generateAttributeValueReportFilePort;

    @Test
    void testCreateAssessmentAttributeAiReport_AssessmentNotFound_ThrowResourceNotFoundException() {
        UUID assessmentId = UUID.randomUUID();
        Param param = new Param(assessmentId, 123L, UUID.randomUUID());

        when(getAssessmentPort.getAssessmentById(param.getAssessmentId())).thenReturn(Optional.empty());

        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.createAttributeAiReport(param));
        assertEquals(ASSESSMENT_ID_NOT_FOUND, throwable.getMessage());

        verifyNoInteractions(assessmentAccessChecker,
            loadAttributePort,
            loadAssessmentResultPort,
            loadAttributeInsightPort,
            createAttributeAiInsightPort,
            updateAttributeInsightPort);
    }

    @Test
    void testCreateAssessmentAttributeAiReport_UserDoesNotHaveRequiredPermission_ThrowAccessDeniedException() {
        UUID currentUserId = UUID.randomUUID();
        var assessment = AssessmentMother.assessment();
        Param param = new Param(assessment.getId(), 123L, currentUserId);

        when(getAssessmentPort.getAssessmentById(param.getAssessmentId())).thenReturn(Optional.of(assessment));
        when(assessmentAccessChecker.isAuthorized(assessment.getId(), param.getCurrentUserId(), EXPORT_ASSESSMENT_REPORT)).thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.createAttributeAiReport(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verifyNoInteractions(loadAttributePort,
            loadAssessmentResultPort,
            loadAttributeInsightPort,
            createAttributeAiInsightPort,
            updateAttributeInsightPort,
            loadAttributeValuePort,
            loadMaturityLevelsPort,
            generateAttributeValueReportFilePort);
    }

    @Test
    void testCreateAssessmentAttributeAiReport_AssessmentResultNotFound_ThrowResourceNotFoundException() {
        UUID currentUserId = UUID.randomUUID();
        var assessment = AssessmentMother.assessment();
        Param param = new Param(assessment.getId(), 123L, currentUserId);

        when(getAssessmentPort.getAssessmentById(param.getAssessmentId())).thenReturn(Optional.of(assessment));
        when(assessmentAccessChecker.isAuthorized(assessment.getId(), param.getCurrentUserId(), EXPORT_ASSESSMENT_REPORT)).thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(assessment.getId()))
            .thenThrow(new ResourceNotFoundException(CREATE_ASSESSMENT_ATTRIBUTE_AI_REPORT_ASSESSMENT_RESULT_NOT_FOUND));

        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.createAttributeAiReport(param));
        assertEquals(CREATE_ASSESSMENT_ATTRIBUTE_AI_REPORT_ASSESSMENT_RESULT_NOT_FOUND, throwable.getMessage());

        verifyNoInteractions(loadAttributeInsightPort,
            createAttributeAiInsightPort,
            updateAttributeInsightPort);
    }

    @Test
    void testCreateAssessmentAttributeAiReport_CalculatedResultIsNotValid_ThrowCalculateNotValidException() {
        Long attributeId = 1L;
        UUID currentUserId = UUID.randomUUID();
        var assessmentResult = AssessmentResultMother.invalidResultWithSubjectValues(null);
        var assessment = assessmentResult.getAssessment();

        Param param = new Param(assessment.getId(), attributeId, currentUserId);

        when(getAssessmentPort.getAssessmentById(param.getAssessmentId())).thenReturn(Optional.of(assessment));
        when(assessmentAccessChecker.isAuthorized(assessment.getId(), param.getCurrentUserId(), EXPORT_ASSESSMENT_REPORT)).thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(assessment.getId())).thenReturn(Optional.of(assessmentResult));
        doThrow(new CalculateNotValidException(COMMON_ASSESSMENT_RESULT_NOT_VALID)).when(validateAssessmentResultPort).validate(assessment.getId());

        var throwable = assertThrows(CalculateNotValidException.class, () -> service.createAttributeAiReport(param));
        assertEquals(COMMON_ASSESSMENT_RESULT_NOT_VALID, throwable.getMessage());

        verifyNoInteractions(loadAttributeInsightPort,
            createAttributeAiInsightPort,
            updateAttributeInsightPort);
    }

    @Test
    void testCreateAssessmentAttributeAiReport_AiInsightDoesNotExistAndAiEnabled_GenerateAndPersistAiInsight() {
        UUID currentUserId = UUID.randomUUID();
        var attribute = AttributeMother.simpleAttribute();
        var assessmentResult = AssessmentResultMother.validResultWithJustAnId();
        var assessment = assessmentResult.getAssessment();
        Param param = new Param(assessment.getId(), attribute.getId(), currentUserId);
        InputStream inputStream = new ByteArrayInputStream("File Content".getBytes());
        AttributeValue attributeValue = AttributeValueMother.toBeCalcAsLevelThreeWithWeight(1);
        List<MaturityLevel> maturityLevels = MaturityLevelMother.allLevels();
        var aiReport = "Report Content";

        when(openAiProperties.isEnabled()).thenReturn(true);
        when(getAssessmentPort.getAssessmentById(param.getAssessmentId())).thenReturn(Optional.of(assessment));
        when(assessmentAccessChecker.isAuthorized(assessment.getId(), param.getCurrentUserId(), EXPORT_ASSESSMENT_REPORT)).thenReturn(true);
        doNothing().when(validateAssessmentResultPort).validate(assessment.getId());
        when(loadAssessmentResultPort.loadByAssessmentId(assessment.getId())).thenReturn(Optional.of(assessmentResult));
        when(loadAttributePort.load(attribute.getId(), assessmentResult.getKitVersionId())).thenReturn(attribute);
        when(loadAttributeInsightPort.loadAttributeAiInsight(assessmentResult.getId(), param.getAttributeId())).thenReturn(Optional.empty());
        when(createAttributeAiInsightPort.generateInsight(inputStream, attribute)).thenReturn(aiReport);
        when(loadAttributeValuePort.load(assessmentResult.getId(), param.getAttributeId())).thenReturn(attributeValue);
        when(loadMaturityLevelsPort.loadByKitVersionId(assessmentResult.getKitVersionId())).thenReturn(maturityLevels);
        when(generateAttributeValueReportFilePort.generateFile(attributeValue, maturityLevels))
            .thenReturn(inputStream);
        doNothing().when(createAttributeInsightPort).persist(any());

        var result = service.createAttributeAiReport(param);
        assertEquals("Report Content", result.content());

        verifyNoInteractions(updateAttributeInsightPort);
    }

    @Test
    void testCreateAssessmentAttributeAiReport_AiInsightDoesNotExistAndAiDisabled_ReturnConstantMessage() {
        UUID currentUserId = UUID.randomUUID();
        var attribute = AttributeMother.simpleAttribute();
        var assessmentResult = AssessmentResultMother.validResultWithJustAnId();
        var assessment = assessmentResult.getAssessment();
        Param param = new Param(assessment.getId(), attribute.getId(), currentUserId);
        AttributeValue attributeValue = AttributeValueMother.toBeCalcAsLevelThreeWithWeight(1);
        List<MaturityLevel> maturityLevels = MaturityLevelMother.allLevels();

        when(openAiProperties.isEnabled()).thenReturn(false);
        when(getAssessmentPort.getAssessmentById(param.getAssessmentId())).thenReturn(Optional.of(assessment));
        when(assessmentAccessChecker.isAuthorized(assessment.getId(), param.getCurrentUserId(), EXPORT_ASSESSMENT_REPORT)).thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(assessment.getId())).thenReturn(Optional.of(assessmentResult));
        when(loadAttributePort.load(attribute.getId(), assessmentResult.getKitVersionId())).thenReturn(attribute);
        when(loadAttributeInsightPort.loadAttributeAiInsight(assessmentResult.getId(), param.getAttributeId())).thenReturn(Optional.empty());
        when(loadAttributeValuePort.load(assessmentResult.getId(), param.getAttributeId())).thenReturn(attributeValue);
        when(loadMaturityLevelsPort.loadByKitVersionId(assessmentResult.getKitVersionId())).thenReturn(maturityLevels);

        var result = service.createAttributeAiReport(param);
        assertEquals(MessageBundle.message(ASSESSMENT_ATTRIBUTE_AI_IS_DISABLED, attribute.getTitle()), result.content());

        verifyNoInteractions(updateAttributeInsightPort, createAttributeAiInsightPort, createAttributeInsightPort);
    }

    @Test
    void testCreateAssessmentAttributeAiReport_AiInsightExistsAndInsightTimeIsAfterCalculationTime_ReturnExistedInsight() {
        UUID currentUserId = UUID.randomUUID();
        UUID assessmentId = UUID.randomUUID();
        var attribute = AttributeMother.simpleAttribute();
        var assessmentResult = AssessmentResultMother.validResultWithJustAnId();
        var assessment = assessmentResult.getAssessment();
        Param param = new Param(assessmentId, attribute.getId(), currentUserId);
        var attributeInsight = AttributeInsightMother.simpleAttributeAiInsight();

        when(getAssessmentPort.getAssessmentById(param.getAssessmentId())).thenReturn(Optional.of(assessment));
        when(assessmentAccessChecker.isAuthorized(assessment.getId(), param.getCurrentUserId(), EXPORT_ASSESSMENT_REPORT)).thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(assessment.getId())).thenReturn(Optional.of(assessmentResult));
        when(loadAttributePort.load(attribute.getId(), assessmentResult.getKitVersionId())).thenReturn(attribute);
        when(loadAttributeInsightPort.loadAttributeAiInsight(assessmentResult.getId(), param.getAttributeId()))
            .thenReturn(Optional.of(attributeInsight));

        var result = service.createAttributeAiReport(param);
        assertEquals(result.content(), attributeInsight.getAiInsight());

        verifyNoInteractions(createAttributeAiInsightPort, updateAttributeInsightPort);
    }

    @Test
    void testCreateAssessmentAttributeAiReport_AiInsightExistsAndInsightTimeIsBeforeCalculationTime_AiEnabled_RegenerateAndUpdateInsight() {
        UUID currentUserId = UUID.randomUUID();
        UUID assessmentId = UUID.randomUUID();
        var attribute = AttributeMother.simpleAttribute();
        var assessmentResult = AssessmentResultMother.validResultWithJustAnId();
        var assessment = assessmentResult.getAssessment();
        Param param = new Param(assessmentId, attribute.getId(), currentUserId);
        var attributeInsight = simpleAttributeAiInsightMinInsightTime();
        InputStream inputStream = new ByteArrayInputStream("File Content".getBytes());
        AttributeValue attributeValue = AttributeValueMother.toBeCalcAsLevelThreeWithWeight(1);
        List<MaturityLevel> maturityLevels = MaturityLevelMother.allLevels();

        when(openAiProperties.isEnabled()).thenReturn(true);
        when(getAssessmentPort.getAssessmentById(param.getAssessmentId())).thenReturn(Optional.of(assessment));
        when(assessmentAccessChecker.isAuthorized(assessment.getId(), param.getCurrentUserId(), EXPORT_ASSESSMENT_REPORT)).thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(assessment.getId())).thenReturn(Optional.of(assessmentResult));
        when(loadAttributePort.load(attribute.getId(), assessmentResult.getKitVersionId())).thenReturn(attribute);
        when(loadAttributeInsightPort.loadAttributeAiInsight(assessmentResult.getId(), param.getAttributeId())).thenReturn(Optional.of(attributeInsight));
        when(createAttributeAiInsightPort.generateInsight(inputStream, attribute)).thenReturn(attributeInsight.getAiInsight());
        when(loadAttributeValuePort.load(assessmentResult.getId(), param.getAttributeId())).thenReturn(attributeValue);
        when(loadMaturityLevelsPort.loadByKitVersionId(assessmentResult.getKitVersionId())).thenReturn(maturityLevels);
        when(generateAttributeValueReportFilePort.generateFile(attributeValue, maturityLevels))
            .thenReturn(inputStream);
        doNothing().when(updateAttributeInsightPort).updateAiInsight(any());

        var result = service.createAttributeAiReport(param);
        assertEquals(attributeInsight.getAiInsight(), result.content());

        verifyNoInteractions(createAttributeInsightPort);
    }

    @Test
    void testCreateAssessmentAttributeAiReport_AiInsightExistsAndInsightTimeIsBeforeCalculationTime_AiDisabled_ReturnConstantMessage() {
        UUID currentUserId = UUID.randomUUID();
        UUID assessmentId = UUID.randomUUID();
        var attribute = AttributeMother.simpleAttribute();
        var assessmentResult = AssessmentResultMother.validResultWithJustAnId();
        var assessment = assessmentResult.getAssessment();
        Param param = new Param(assessmentId, attribute.getId(), currentUserId);
        var attributeInsight = simpleAttributeAiInsightMinInsightTime();

        when(openAiProperties.isEnabled()).thenReturn(false);
        when(getAssessmentPort.getAssessmentById(param.getAssessmentId())).thenReturn(Optional.of(assessment));
        when(assessmentAccessChecker.isAuthorized(assessment.getId(), param.getCurrentUserId(), EXPORT_ASSESSMENT_REPORT)).thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(assessment.getId())).thenReturn(Optional.of(assessmentResult));
        when(loadAttributePort.load(attribute.getId(), assessmentResult.getKitVersionId())).thenReturn(attribute);
        when(loadAttributeInsightPort.loadAttributeAiInsight(assessmentResult.getId(), param.getAttributeId())).thenReturn(Optional.of(attributeInsight));

        var result = service.createAttributeAiReport(param);
        assertEquals(MessageBundle.message(ASSESSMENT_ATTRIBUTE_AI_IS_DISABLED, attribute.getTitle()), result.content());

        verifyNoInteractions(createAttributeAiInsightPort, createAttributeInsightPort, updateAttributeInsightPort);
    }
}
