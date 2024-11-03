package org.flickit.assessment.core.application.service.attribute;

import org.flickit.assessment.common.application.MessageBundle;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.application.port.out.CallAiPromptPort;
import org.flickit.assessment.common.application.port.out.ValidateAssessmentResultPort;
import org.flickit.assessment.common.config.AppAiProperties;
import org.flickit.assessment.common.config.OpenAiProperties;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.CalculateNotValidException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.common.exception.ValidationException;
import org.flickit.assessment.core.application.domain.AttributeValue;
import org.flickit.assessment.core.application.domain.MaturityLevel;
import org.flickit.assessment.core.application.port.in.attribute.CreateAttributeAiInsightUseCase.Param;
import org.flickit.assessment.core.application.port.out.assessment.GetAssessmentProgressPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.core.application.port.out.attribute.CreateAttributeScoresFilePort;
import org.flickit.assessment.core.application.port.out.attribute.LoadAttributePort;
import org.flickit.assessment.core.application.port.out.attributeinsight.CreateAttributeInsightPort;
import org.flickit.assessment.core.application.port.out.attributeinsight.LoadAttributeInsightPort;
import org.flickit.assessment.core.application.port.out.attributeinsight.UpdateAttributeInsightPort;
import org.flickit.assessment.core.application.port.out.attributevalue.LoadAttributeValuePort;
import org.flickit.assessment.core.application.port.out.maturitylevel.LoadMaturityLevelsPort;
import org.flickit.assessment.core.application.port.out.minio.UploadAttributeScoresFilePort;
import org.flickit.assessment.core.test.fixture.application.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.prompt.Prompt;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.CREATE_ATTRIBUTE_INSIGHT;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_ASSESSMENT_RESULT_NOT_VALID;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.core.common.ErrorMessageKey.CREATE_ATTRIBUTE_AI_INSIGHT_ALL_QUESTIONS_NOT_ANSWERED;
import static org.flickit.assessment.core.common.ErrorMessageKey.CREATE_ATTRIBUTE_AI_INSIGHT_ASSESSMENT_RESULT_NOT_FOUND;
import static org.flickit.assessment.core.common.MessageKey.ASSESSMENT_AI_IS_DISABLED;
import static org.flickit.assessment.core.test.fixture.application.AttributeInsightMother.simpleAttributeAiInsightMinInsightTime;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateAttributeAiInsightServiceTest {

    @InjectMocks
    private CreateAttributeAiInsightService service;

    @Mock
    private AppAiProperties appAiProperties;

    @Mock
    private ValidateAssessmentResultPort validateAssessmentResultPort;

    @Mock
    private LoadAttributeValuePort loadAttributeValuePort;

    @Mock
    private LoadMaturityLevelsPort loadMaturityLevelsPort;

    @Mock
    private AssessmentAccessChecker assessmentAccessChecker;

    @Mock
    private LoadAssessmentResultPort loadAssessmentResultPort;

    @Mock
    private OpenAiProperties openAiProperties;

    @Mock
    private CallAiPromptPort callAiPromptPort;

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

    @Mock
    private UploadAttributeScoresFilePort uploadAttributeScoresFilePort;

    @Mock
    private GetAssessmentProgressPort getAssessmentProgressPort;

    @Test
    void testCreateAttributeAiInsight_UserDoesNotHaveRequiredPermission_ThrowAccessDeniedException() {
        UUID currentUserId = UUID.randomUUID();
        Param param = new Param(UUID.randomUUID(), 123L, currentUserId);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_ATTRIBUTE_INSIGHT)).thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.createAiInsight(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verifyNoInteractions(loadAttributePort,
            loadAssessmentResultPort,
            loadAttributeInsightPort,
            callAiPromptPort,
            updateAttributeInsightPort,
            loadAttributeValuePort,
            loadMaturityLevelsPort,
            generateAttributeValueReportFilePort,
            uploadAttributeScoresFilePort);
    }

    @Test
    void testCreateAttributeAiInsight_AllQuestionDoesNotHaveAnswered_ThrowValidationException() {
        UUID currentUserId = UUID.randomUUID();
        Param param = new Param(UUID.randomUUID(), 123L, currentUserId);
        var progress = new GetAssessmentProgressPort.Result(param.getAssessmentId(), 10, 11);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_ATTRIBUTE_INSIGHT)).thenReturn(true);
        when(getAssessmentProgressPort.getProgress(param.getAssessmentId())).thenReturn(progress);

        var throwable = assertThrows(ValidationException.class, () -> service.createAiInsight(param));
        assertEquals(CREATE_ATTRIBUTE_AI_INSIGHT_ALL_QUESTIONS_NOT_ANSWERED, throwable.getMessageKey());

        verifyNoInteractions(loadAttributePort,
            loadAssessmentResultPort,
            loadAttributeInsightPort,
            callAiPromptPort,
            updateAttributeInsightPort,
            loadAttributeValuePort,
            loadMaturityLevelsPort,
            generateAttributeValueReportFilePort,
            uploadAttributeScoresFilePort);
    }

    @Test
    void testCreateAttributeAiInsight_AssessmentResultNotFound_ThrowResourceNotFoundException() {
        UUID currentUserId = UUID.randomUUID();
        Param param = new Param(UUID.randomUUID(), 123L, currentUserId);
        var progress = new GetAssessmentProgressPort.Result(param.getAssessmentId(), 10, 10);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_ATTRIBUTE_INSIGHT)).thenReturn(true);
        when(getAssessmentProgressPort.getProgress(param.getAssessmentId())).thenReturn(progress);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId()))
            .thenThrow(new ResourceNotFoundException(CREATE_ATTRIBUTE_AI_INSIGHT_ASSESSMENT_RESULT_NOT_FOUND));

        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.createAiInsight(param));
        assertEquals(CREATE_ATTRIBUTE_AI_INSIGHT_ASSESSMENT_RESULT_NOT_FOUND, throwable.getMessage());

        verifyNoInteractions(loadAttributeInsightPort,
            callAiPromptPort,
            updateAttributeInsightPort,
            generateAttributeValueReportFilePort,
            uploadAttributeScoresFilePort);
    }

    @Test
    void testCreateAttributeAiInsight_CalculatedResultIsNotValid_ThrowCalculateNotValidException() {
        Long attributeId = 1L;
        UUID currentUserId = UUID.randomUUID();
        var assessmentResult = AssessmentResultMother.invalidResultWithSubjectValues(null);
        Param param = new Param(UUID.randomUUID(), attributeId, currentUserId);
        var progress = new GetAssessmentProgressPort.Result(param.getAssessmentId(), 10, 10);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_ATTRIBUTE_INSIGHT)).thenReturn(true);
        when(getAssessmentProgressPort.getProgress(param.getAssessmentId())).thenReturn(progress);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        doThrow(new CalculateNotValidException(COMMON_ASSESSMENT_RESULT_NOT_VALID)).when(validateAssessmentResultPort).validate(param.getAssessmentId());

        var throwable = assertThrows(CalculateNotValidException.class, () -> service.createAiInsight(param));
        assertEquals(COMMON_ASSESSMENT_RESULT_NOT_VALID, throwable.getMessage());

        verifyNoInteractions(loadAttributeInsightPort,
            callAiPromptPort,
            updateAttributeInsightPort,
            generateAttributeValueReportFilePort,
            uploadAttributeScoresFilePort);
    }

    @Test
    void testCreateAttributeAiInsight_AiInsightDoesNotExistAndAiEnabled_GenerateAndPersistAiInsight() {
        UUID currentUserId = UUID.randomUUID();
        var attribute = AttributeMother.simpleAttribute();
        var assessmentResult = AssessmentResultMother.validResultWithJustAnId();
        Param param = new Param(UUID.randomUUID(), attribute.getId(), currentUserId);
        String fileContent = "file content";
        var file = new CreateAttributeScoresFilePort.Result(new ByteArrayInputStream(fileContent.getBytes()), fileContent);
        AttributeValue attributeValue = AttributeValueMother.hasFullScoreOnLevel23WithWeight(1);
        var fileReportPath = "path/to/file";
        List<MaturityLevel> maturityLevels = MaturityLevelMother.allLevels();
        var aiReport = "Report Content";
        var progress = new GetAssessmentProgressPort.Result(param.getAssessmentId(), 10, 10);
        var prompt = new Prompt("AI prompt");

        when(appAiProperties.isEnabled()).thenReturn(true);
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_ATTRIBUTE_INSIGHT)).thenReturn(true);
        when(getAssessmentProgressPort.getProgress(param.getAssessmentId())).thenReturn(progress);
        doNothing().when(validateAssessmentResultPort).validate(param.getAssessmentId());
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        when(loadAttributePort.load(attribute.getId(), assessmentResult.getKitVersionId())).thenReturn(attribute);
        when(loadAttributeInsightPort.loadAttributeAiInsight(assessmentResult.getId(), param.getAttributeId())).thenReturn(Optional.empty());
        when(openAiProperties.createAttributeAiInsightPrompt(attribute.getTitle(), attribute.getDescription(), file.text())).thenReturn(prompt);
        when(callAiPromptPort.call(prompt)).thenReturn(aiReport);
        when(loadAttributeValuePort.load(assessmentResult.getId(), param.getAttributeId())).thenReturn(attributeValue);
        when(loadMaturityLevelsPort.loadByKitVersionId(assessmentResult.getKitVersionId())).thenReturn(maturityLevels);
        when(generateAttributeValueReportFilePort.generateFile(attributeValue, maturityLevels))
            .thenReturn(file);
        when(appAiProperties.isSaveAiInputFileEnabled()).thenReturn(true);
        when(uploadAttributeScoresFilePort.uploadExcel(eq(file.stream()), any())).thenReturn(fileReportPath);
        doNothing().when(createAttributeInsightPort).persist(any());

        var result = service.createAiInsight(param);
        assertEquals("Report Content", result.content());

        verifyNoInteractions(updateAttributeInsightPort);
    }

    @Test
    void testCreateAttributeAiInsight_AiInsightDoesNotExistAndAiEnabledAndSaveFilesDisabled_GenerateAndNotSaveFileAndPersistInsight() {
        UUID currentUserId = UUID.randomUUID();
        var attribute = AttributeMother.simpleAttribute();
        var assessmentResult = AssessmentResultMother.validResultWithJustAnId();
        Param param = new Param(UUID.randomUUID(), attribute.getId(), currentUserId);
        String fileContent = "file content";
        var file = new CreateAttributeScoresFilePort.Result(new ByteArrayInputStream(fileContent.getBytes()), fileContent);
        AttributeValue attributeValue = AttributeValueMother.hasFullScoreOnLevel23WithWeight(1);
        List<MaturityLevel> maturityLevels = MaturityLevelMother.allLevels();
        var aiReport = "Report Content";
        var progress = new GetAssessmentProgressPort.Result(param.getAssessmentId(), 10, 10);
        var prompt = new Prompt("AI prompt");

        when(appAiProperties.isEnabled()).thenReturn(true);
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_ATTRIBUTE_INSIGHT)).thenReturn(true);
        when(getAssessmentProgressPort.getProgress(param.getAssessmentId())).thenReturn(progress);
        doNothing().when(validateAssessmentResultPort).validate(param.getAssessmentId());
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        when(loadAttributePort.load(attribute.getId(), assessmentResult.getKitVersionId())).thenReturn(attribute);
        when(loadAttributeInsightPort.loadAttributeAiInsight(assessmentResult.getId(), param.getAttributeId())).thenReturn(Optional.empty());
        when(openAiProperties.createAttributeAiInsightPrompt(attribute.getTitle(), attribute.getDescription(), file.text())).thenReturn(prompt);
        when(callAiPromptPort.call(prompt)).thenReturn(aiReport);
        when(loadAttributeValuePort.load(assessmentResult.getId(), param.getAttributeId())).thenReturn(attributeValue);
        when(loadMaturityLevelsPort.loadByKitVersionId(assessmentResult.getKitVersionId())).thenReturn(maturityLevels);
        when(generateAttributeValueReportFilePort.generateFile(attributeValue, maturityLevels))
            .thenReturn(file);
        when(appAiProperties.isSaveAiInputFileEnabled()).thenReturn(false);
        doNothing().when(createAttributeInsightPort).persist(any());

        var result = service.createAiInsight(param);
        assertEquals("Report Content", result.content());

        verifyNoInteractions(updateAttributeInsightPort, uploadAttributeScoresFilePort);
    }

    @Test
    void testCreateAttributeAiInsight_AiInsightDoesNotExistAndAiDisabled_ReturnConstantMessage() {
        UUID currentUserId = UUID.randomUUID();
        var attribute = AttributeMother.simpleAttribute();
        var assessmentResult = AssessmentResultMother.validResultWithJustAnId();
        Param param = new Param(UUID.randomUUID(), attribute.getId(), currentUserId);
        AttributeValue attributeValue = AttributeValueMother.hasFullScoreOnLevel23WithWeight(1);
        List<MaturityLevel> maturityLevels = MaturityLevelMother.allLevels();
        var progress = new GetAssessmentProgressPort.Result(param.getAssessmentId(), 10, 10);

        when(appAiProperties.isEnabled()).thenReturn(false);
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_ATTRIBUTE_INSIGHT)).thenReturn(true);
        when(getAssessmentProgressPort.getProgress(param.getAssessmentId())).thenReturn(progress);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        when(loadAttributePort.load(attribute.getId(), assessmentResult.getKitVersionId())).thenReturn(attribute);
        when(loadAttributeInsightPort.loadAttributeAiInsight(assessmentResult.getId(), param.getAttributeId())).thenReturn(Optional.empty());
        when(loadAttributeValuePort.load(assessmentResult.getId(), param.getAttributeId())).thenReturn(attributeValue);
        when(loadMaturityLevelsPort.loadByKitVersionId(assessmentResult.getKitVersionId())).thenReturn(maturityLevels);

        var result = service.createAiInsight(param);
        assertEquals(MessageBundle.message(ASSESSMENT_AI_IS_DISABLED, attribute.getTitle()), result.content());

        verifyNoInteractions(updateAttributeInsightPort,
            callAiPromptPort,
            createAttributeInsightPort,
            generateAttributeValueReportFilePort,
            uploadAttributeScoresFilePort);
    }

    @Test
    void testCreateAttributeAiInsight_AiInsightExistsAndInsightTimeIsAfterCalculationTime_ReturnExistedInsight() {
        UUID currentUserId = UUID.randomUUID();
        var attribute = AttributeMother.simpleAttribute();
        var assessmentResult = AssessmentResultMother.validResultWithJustAnId();
        Param param = new Param(UUID.randomUUID(), attribute.getId(), currentUserId);
        var attributeInsight = AttributeInsightMother.simpleAttributeAiInsight();
        var progress = new GetAssessmentProgressPort.Result(param.getAssessmentId(), 10, 10);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_ATTRIBUTE_INSIGHT)).thenReturn(true);
        when(getAssessmentProgressPort.getProgress(param.getAssessmentId())).thenReturn(progress);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        when(loadAttributePort.load(attribute.getId(), assessmentResult.getKitVersionId())).thenReturn(attribute);
        when(loadAttributeInsightPort.loadAttributeAiInsight(assessmentResult.getId(), param.getAttributeId()))
            .thenReturn(Optional.of(attributeInsight));

        var result = service.createAiInsight(param);
        assertEquals(result.content(), attributeInsight.getAiInsight());

        verifyNoInteractions(callAiPromptPort,
            updateAttributeInsightPort,
            generateAttributeValueReportFilePort,
            uploadAttributeScoresFilePort);
    }

    @Test
    void testCreateAttributeAiInsight_AiInsightExistsAndInsightTimeIsBeforeCalculationTime_AiEnabled_RegenerateAndUpdateInsight() {
        var attribute = AttributeMother.simpleAttribute();
        var assessmentResult = AssessmentResultMother.validResultWithJustAnId();
        Param param = new Param(UUID.randomUUID(), attribute.getId(), UUID.randomUUID());
        var attributeInsight = simpleAttributeAiInsightMinInsightTime();
        String fileContent = "file content";
        var file = new CreateAttributeScoresFilePort.Result(new ByteArrayInputStream(fileContent.getBytes()), fileContent);
        var fileReportPath = "path/to/file";
        AttributeValue attributeValue = AttributeValueMother.hasFullScoreOnLevel23WithWeight(1);
        List<MaturityLevel> maturityLevels = MaturityLevelMother.allLevels();
        var progress = new GetAssessmentProgressPort.Result(param.getAssessmentId(), 10, 10);
        var prompt = new Prompt("AI prompt");

        when(appAiProperties.isEnabled()).thenReturn(true);
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_ATTRIBUTE_INSIGHT)).thenReturn(true);
        when(getAssessmentProgressPort.getProgress(param.getAssessmentId())).thenReturn(progress);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        when(loadAttributePort.load(attribute.getId(), assessmentResult.getKitVersionId())).thenReturn(attribute);
        when(loadAttributeInsightPort.loadAttributeAiInsight(assessmentResult.getId(), param.getAttributeId())).thenReturn(Optional.of(attributeInsight));
        when(openAiProperties.createAttributeAiInsightPrompt(attribute.getTitle(), attribute.getDescription(), file.text())).thenReturn(prompt);
        when(callAiPromptPort.call(prompt)).thenReturn(attributeInsight.getAiInsight());
        when(loadAttributeValuePort.load(assessmentResult.getId(), param.getAttributeId())).thenReturn(attributeValue);
        when(loadMaturityLevelsPort.loadByKitVersionId(assessmentResult.getKitVersionId())).thenReturn(maturityLevels);
        when(generateAttributeValueReportFilePort.generateFile(attributeValue, maturityLevels))
            .thenReturn(file);
        when(appAiProperties.isSaveAiInputFileEnabled()).thenReturn(true);
        when(uploadAttributeScoresFilePort.uploadExcel(eq(file.stream()), any())).thenReturn(fileReportPath);
        doNothing().when(updateAttributeInsightPort).updateAiInsight(any());

        var result = service.createAiInsight(param);
        assertEquals(attributeInsight.getAiInsight(), result.content());

        verifyNoInteractions(createAttributeInsightPort);
    }

    @Test
    void testCreateAttributeAiInsight_AiInsightExistsAndInsightTimeIsBeforeCalculationTime_AiEnabledSaveFilesDisabled_RegenerateAndNotSaveFileAndUpdateInsight() {
        var attribute = AttributeMother.simpleAttribute();
        var assessmentResult = AssessmentResultMother.validResultWithJustAnId();
        var assessment = assessmentResult.getAssessment();
        Param param = new Param(assessment.getId(), attribute.getId(), UUID.randomUUID());
        var attributeInsight = simpleAttributeAiInsightMinInsightTime();
        String fileContent = "file content";
        var file = new CreateAttributeScoresFilePort.Result(new ByteArrayInputStream(fileContent.getBytes()), fileContent);
        AttributeValue attributeValue = AttributeValueMother.hasFullScoreOnLevel23WithWeight(1);
        List<MaturityLevel> maturityLevels = MaturityLevelMother.allLevels();
        var progress = new GetAssessmentProgressPort.Result(param.getAssessmentId(), 10, 10);
        var prompt = new Prompt("AI prompt");

        when(appAiProperties.isEnabled()).thenReturn(true);
        when(assessmentAccessChecker.isAuthorized(assessment.getId(), param.getCurrentUserId(), CREATE_ATTRIBUTE_INSIGHT)).thenReturn(true);
        when(getAssessmentProgressPort.getProgress(param.getAssessmentId())).thenReturn(progress);
        when(loadAssessmentResultPort.loadByAssessmentId(assessment.getId())).thenReturn(Optional.of(assessmentResult));
        when(loadAttributePort.load(attribute.getId(), assessmentResult.getKitVersionId())).thenReturn(attribute);
        when(loadAttributeInsightPort.loadAttributeAiInsight(assessmentResult.getId(), param.getAttributeId())).thenReturn(Optional.of(attributeInsight));
        when(openAiProperties.createAttributeAiInsightPrompt(attribute.getTitle(), attribute.getDescription(), file.text())).thenReturn(prompt);
        when(callAiPromptPort.call(prompt)).thenReturn(attributeInsight.getAiInsight());
        when(loadAttributeValuePort.load(assessmentResult.getId(), param.getAttributeId())).thenReturn(attributeValue);
        when(loadMaturityLevelsPort.loadByKitVersionId(assessmentResult.getKitVersionId())).thenReturn(maturityLevels);
        when(generateAttributeValueReportFilePort.generateFile(attributeValue, maturityLevels))
            .thenReturn(file);
        when(appAiProperties.isSaveAiInputFileEnabled()).thenReturn(false);
        doNothing().when(updateAttributeInsightPort).updateAiInsight(any());

        var result = service.createAiInsight(param);
        assertEquals(attributeInsight.getAiInsight(), result.content());

        verifyNoInteractions(createAttributeInsightPort, uploadAttributeScoresFilePort);
    }

    @Test
    void testCreateAttributeAiInsight_AiInsightExistsAndInsightTimeIsBeforeCalculationTime_AiDisabled_ReturnConstantMessage() {
        UUID currentUserId = UUID.randomUUID();
        var attribute = AttributeMother.simpleAttribute();
        var assessmentResult = AssessmentResultMother.validResultWithJustAnId();
        Param param = new Param(UUID.randomUUID(), attribute.getId(), currentUserId);
        var attributeInsight = simpleAttributeAiInsightMinInsightTime();
        var progress = new GetAssessmentProgressPort.Result(param.getAssessmentId(), 10, 10);

        when(appAiProperties.isEnabled()).thenReturn(false);
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_ATTRIBUTE_INSIGHT)).thenReturn(true);
        when(getAssessmentProgressPort.getProgress(param.getAssessmentId())).thenReturn(progress);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        when(loadAttributePort.load(attribute.getId(), assessmentResult.getKitVersionId())).thenReturn(attribute);
        when(loadAttributeInsightPort.loadAttributeAiInsight(assessmentResult.getId(), param.getAttributeId())).thenReturn(Optional.of(attributeInsight));

        var result = service.createAiInsight(param);
        assertEquals(MessageBundle.message(ASSESSMENT_AI_IS_DISABLED, attribute.getTitle()), result.content());

        verifyNoInteractions(callAiPromptPort,
            createAttributeInsightPort,
            updateAttributeInsightPort,
            generateAttributeValueReportFilePort,
            uploadAttributeScoresFilePort);
    }
}
