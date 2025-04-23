package org.flickit.assessment.core.application.service.insight.attribute;

import org.flickit.assessment.common.application.port.out.CallAiPromptPort;
import org.flickit.assessment.common.config.AppAiProperties;
import org.flickit.assessment.common.exception.ValidationException;
import org.flickit.assessment.core.application.domain.AssessmentResult;
import org.flickit.assessment.core.application.domain.AttributeValue;
import org.flickit.assessment.core.application.domain.MaturityLevel;
import org.flickit.assessment.core.application.port.out.assessment.GetAssessmentProgressPort;
import org.flickit.assessment.core.application.port.out.attribute.CreateAttributeScoresFilePort;
import org.flickit.assessment.core.application.port.out.attributevalue.LoadAttributeValuePort;
import org.flickit.assessment.core.application.port.out.maturitylevel.LoadMaturityLevelsPort;
import org.flickit.assessment.core.application.port.out.minio.UploadAttributeScoresFilePort;
import org.flickit.assessment.core.application.service.insight.attribute.CreateAttributeAiInsightHelper.AttributeInsightParam;
import org.flickit.assessment.core.application.service.insight.attribute.CreateAttributeAiInsightHelper.AttributeInsightsParam;
import org.flickit.assessment.core.test.fixture.application.AttributeValueMother;
import org.flickit.assessment.core.test.fixture.application.MaturityLevelMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.prompt.Prompt;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

import static org.flickit.assessment.core.common.ErrorMessageKey.CREATE_ATTRIBUTE_AI_INSIGHT_ALL_QUESTIONS_NOT_ANSWERED;
import static org.flickit.assessment.core.common.MessageKey.ASSESSMENT_AI_IS_DISABLED;
import static org.flickit.assessment.core.test.fixture.application.AssessmentResultMother.validResult;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateAttributeAiInsightHelperTest {

    @InjectMocks
    private CreateAttributeAiInsightHelper helper;

    @Mock
    private LoadAttributeValuePort loadAttributeValuePort;

    @Mock
    private LoadMaturityLevelsPort loadMaturityLevelsPort;

    @Mock
    private GetAssessmentProgressPort getAssessmentProgressPort;

    @Spy
    private AppAiProperties appAiProperties = appAiProperties();

    @Mock
    private CreateAttributeScoresFilePort createAttributeScoresFilePort;

    @Mock
    private UploadAttributeScoresFilePort uploadAttributeScoresFilePort;

    @Mock
    private CallAiPromptPort callAiPromptPort;

    @Captor
    private ArgumentCaptor<Class<CreateAttributeAiInsightHelper.AiResponseDto>> classCaptor;

    @Captor
    private ArgumentCaptor<Prompt> promptArgumentCaptor;

    private final AssessmentResult assessmentResult = validResult();
    private final GetAssessmentProgressPort.Result completeProgress = new GetAssessmentProgressPort.Result(assessmentResult.getId(), 10, 10);
    private final String fileContent = "file content";
    private final CreateAttributeAiInsightHelper.AiResponseDto aiInsight = new CreateAttributeAiInsightHelper.AiResponseDto("Insight Content");
    private final CreateAttributeScoresFilePort.Result file = new CreateAttributeScoresFilePort.Result(new ByteArrayInputStream(fileContent.getBytes()), fileContent);
    private final AttributeValue attributeValue = AttributeValueMother.hasFullScoreOnLevel23WithWeight(1, 159L);
    private final List<MaturityLevel> maturityLevels = MaturityLevelMother.allLevels();
    private final AttributeInsightParam attributeInsightParam = createParam(AttributeInsightParam.AttributeInsightParamBuilder::build);
    private final AttributeInsightsParam attributeInsightsParam = createParams(AttributeInsightsParam.AttributeInsightsParamBuilder::build);

    @Test
    void testCreateAttributeAiInsightHelper_whenAssessmentProgressIsNotCompleted_thenThrowValidationException() {
        var incompleteProgress = new GetAssessmentProgressPort.Result(attributeInsightParam.assessmentResult().getId(), 10, 11);

        when(getAssessmentProgressPort.getProgress(assessmentResult.getAssessment().getId())).thenReturn(incompleteProgress);
        var throwable = assertThrows(ValidationException.class, () -> helper.createAttributeAiInsight(attributeInsightParam));
        assertEquals(CREATE_ATTRIBUTE_AI_INSIGHT_ALL_QUESTIONS_NOT_ANSWERED, throwable.getMessageKey());

        verifyNoInteractions(loadAttributeValuePort,
            loadMaturityLevelsPort,
            appAiProperties,
            createAttributeScoresFilePort,
            uploadAttributeScoresFilePort,
            callAiPromptPort);
    }

    @Test
    void testCreateAttributeAiInsightHelper_whenAiDisabled_thenThrowUnsupportedOperationException() {
        when(getAssessmentProgressPort.getProgress(assessmentResult.getAssessment().getId())).thenReturn(completeProgress);
        when(loadAttributeValuePort.load(attributeInsightParam.assessmentResult().getId(), attributeInsightParam.attributeId())).thenReturn(attributeValue);
        when(appAiProperties.isEnabled()).thenReturn(false);

        var throwable = assertThrows(UnsupportedOperationException.class, () -> helper.createAttributeAiInsight(attributeInsightParam));
        assertEquals(ASSESSMENT_AI_IS_DISABLED, throwable.getMessage());

        verifyNoInteractions(createAttributeScoresFilePort,
            uploadAttributeScoresFilePort,
            callAiPromptPort);
    }

    @Test
    void testCreateAttributeAiInsightHelper_whenAiEnabledAndSaveFileEnabled_thenGenerateAndUploadFile() {
        var expectedPrompt = "The attribute " + attributeValue.getAttribute().getTitle() + " with this description " + attributeValue.getAttribute().getDescription() +
            " for " + attributeInsightParam.assessmentResult().getAssessment().getShortTitle() + " was reviewed in " + fileContent + ". " +
            "Provide the result in " + attributeInsightParam.assessmentResult().getAssessment().getAssessmentKit().getLanguage().getTitle() + ".";
        var fileReportPath = "path/to/file";

        when(getAssessmentProgressPort.getProgress(assessmentResult.getAssessment().getId())).thenReturn(completeProgress);
        when(loadAttributeValuePort.load(attributeInsightParam.assessmentResult().getId(), attributeInsightParam.attributeId())).thenReturn(attributeValue);
        when(loadMaturityLevelsPort.loadByAssessmentId(attributeInsightsParam.assessmentResult().getAssessment().getId()))
            .thenReturn(maturityLevels);
        when(createAttributeScoresFilePort.generateFile(attributeValue, maturityLevels)).thenReturn(file);
        when(uploadAttributeScoresFilePort.uploadExcel(eq(file.stream()), any())).thenReturn(fileReportPath);
        when(callAiPromptPort.call(promptArgumentCaptor.capture(), classCaptor.capture())).thenReturn(aiInsight);

        var result = helper.createAttributeAiInsight(attributeInsightParam);
        assertEquals("Insight Content", result.getAiInsight());
        assertEquals(aiInsight.value(), result.getAiInsight());
        assertNotNull(result.getAiInsightTime());
        assertNull(result.getAssessorInsight());
        assertNull(result.getAssessorInsightTime());
        assertEquals(fileReportPath, result.getAiInputPath());
        assertFalse(result.isApproved());
        assertNotNull(result.getLastModificationTime());
        assertEquals(assessmentResult.getId(), result.getAssessmentResultId());
        assertEquals(expectedPrompt, promptArgumentCaptor.getValue().getContents());
    }

    @Test
    void testCreateAttributeAiInsightHelper_whenAiEnabledAndSaveFilesDisabled_thenGenerateAndNotSaveFileAndPersistInsight() {
        String expectedPrompt = "The attribute " + attributeValue.getAttribute().getTitle() + " with this description " +
            attributeValue.getAttribute().getDescription() + " for " + attributeInsightParam.assessmentResult().getAssessment().getShortTitle() +
            " was reviewed in " + fileContent + ". " + "Provide the result in " +
            attributeInsightParam.assessmentResult().getAssessment().getAssessmentKit().getLanguage().getTitle() + ".";

        when(getAssessmentProgressPort.getProgress(assessmentResult.getAssessment().getId())).thenReturn(completeProgress);
        when(loadAttributeValuePort.load(attributeInsightParam.assessmentResult().getId(), attributeInsightParam.attributeId())).thenReturn(attributeValue);
        when(appAiProperties.isSaveAiInputFileEnabled()).thenReturn(false);
        when(loadMaturityLevelsPort.loadByAssessmentId(attributeInsightsParam.assessmentResult().getAssessment().getId()))
            .thenReturn(maturityLevels);
        when(createAttributeScoresFilePort.generateFile(attributeValue, maturityLevels)).thenReturn(file);
        when(callAiPromptPort.call(promptArgumentCaptor.capture(), classCaptor.capture())).thenReturn(aiInsight);

        var result = helper.createAttributeAiInsight(attributeInsightParam);
        assertEquals("Insight Content", result.getAiInsight());
        assertEquals(aiInsight.value(), result.getAiInsight());
        assertNotNull(result.getAiInsightTime());
        assertNull(result.getAssessorInsight());
        assertNull(result.getAssessorInsightTime());
        assertNull(result.getAiInputPath());
        assertFalse(result.isApproved());
        assertNotNull(result.getLastModificationTime());
        assertEquals(attributeInsightParam.assessmentResult().getId(), result.getAssessmentResultId());
        assertEquals(expectedPrompt, promptArgumentCaptor.getValue().getContents());

        verifyNoInteractions(uploadAttributeScoresFilePort);
    }

    @Test
    void testCreateAttributeAiInsightsHelper_whenAssessmentProgressIsNotCompleted_thenThrowValidationException() {
        var incompleteProgress = new GetAssessmentProgressPort.Result(attributeInsightParam.assessmentResult().getId(), 10, 11);

        when(getAssessmentProgressPort.getProgress(assessmentResult.getAssessment().getId())).thenReturn(incompleteProgress);
        var throwable = assertThrows(ValidationException.class, () -> helper.createAttributeAiInsights(attributeInsightsParam));
        assertEquals(CREATE_ATTRIBUTE_AI_INSIGHT_ALL_QUESTIONS_NOT_ANSWERED, throwable.getMessageKey());

        verifyNoInteractions(loadAttributeValuePort,
            loadMaturityLevelsPort,
            appAiProperties,
            createAttributeScoresFilePort,
            uploadAttributeScoresFilePort,
            callAiPromptPort);
    }

    @Test
    void testCreateAttributeAiInsightsHelper_whenAiDisabled_thenThrowUnsupportedOperationException() {
        when(appAiProperties.isEnabled()).thenReturn(false);

        when(getAssessmentProgressPort.getProgress(assessmentResult.getAssessment().getId())).thenReturn(completeProgress);
        var throwable = assertThrows(UnsupportedOperationException.class, () -> helper.createAttributeAiInsights(attributeInsightsParam));
        assertEquals(ASSESSMENT_AI_IS_DISABLED, throwable.getMessage());

        verifyNoInteractions(createAttributeScoresFilePort,
            loadMaturityLevelsPort,
            loadAttributeValuePort,
            uploadAttributeScoresFilePort,
            callAiPromptPort);
    }

    @Test
    void testCreateAttributeAiInsightsHelper_whenAiEnabledAndSaveFileEnabled_thenGenerateAndUploadFile() {
        var expectedPrompt = "The attribute " + attributeValue.getAttribute().getTitle() + " with this description " + attributeValue.getAttribute().getDescription() +
            " for " + attributeInsightParam.assessmentResult().getAssessment().getShortTitle() + " was reviewed in " + fileContent + ". " +
            "Provide the result in " + attributeInsightParam.assessmentResult().getAssessment().getAssessmentKit().getLanguage().getTitle() + ".";
        var fileReportPath = "path/to/file";

        when(getAssessmentProgressPort.getProgress(assessmentResult.getAssessment().getId())).thenReturn(completeProgress);
        when(loadAttributeValuePort.load(attributeInsightParam.assessmentResult().getId(), attributeInsightsParam.attributeIds()))
            .thenReturn(List.of(attributeValue));
        when(loadMaturityLevelsPort.loadByAssessmentId(attributeInsightsParam.assessmentResult().getAssessment().getId()))
            .thenReturn(maturityLevels);
        when(createAttributeScoresFilePort.generateFile(attributeValue, maturityLevels)).thenReturn(file);
        when(uploadAttributeScoresFilePort.uploadExcel(eq(file.stream()), any())).thenReturn(fileReportPath);
        when(callAiPromptPort.call(promptArgumentCaptor.capture(), classCaptor.capture())).thenReturn(aiInsight);

        var result = helper.createAttributeAiInsights(attributeInsightsParam).getFirst();
        assertEquals("Insight Content", result.getAiInsight());
        assertEquals(aiInsight.value(), result.getAiInsight());
        assertNotNull(result.getAiInsightTime());
        assertNull(result.getAssessorInsight());
        assertNull(result.getAssessorInsightTime());
        assertEquals(fileReportPath, result.getAiInputPath());
        assertFalse(result.isApproved());
        assertNotNull(result.getLastModificationTime());
        assertEquals(assessmentResult.getId(), result.getAssessmentResultId());
        assertEquals(expectedPrompt, promptArgumentCaptor.getValue().getContents());
    }

    @Test
    void testCreateAttributeAiInsightsHelper_whenAiEnabledAndSaveFilesDisabled_thenGenerateAndNotSaveFileAndPersistInsight() {
        String expectedPrompt = "The attribute " + attributeValue.getAttribute().getTitle() + " with this description " +
            attributeValue.getAttribute().getDescription() + " for " + attributeInsightParam.assessmentResult().getAssessment().getShortTitle() +
            " was reviewed in " + fileContent + ". " + "Provide the result in " +
            attributeInsightParam.assessmentResult().getAssessment().getAssessmentKit().getLanguage().getTitle() + ".";

        when(getAssessmentProgressPort.getProgress(assessmentResult.getAssessment().getId())).thenReturn(completeProgress);
        when(loadAttributeValuePort.load(attributeInsightParam.assessmentResult().getId(), attributeInsightsParam.attributeIds()))
            .thenReturn(List.of(attributeValue));
        when(loadMaturityLevelsPort.loadByAssessmentId(attributeInsightsParam.assessmentResult().getAssessment().getId()))
            .thenReturn(maturityLevels);
        when(appAiProperties.isSaveAiInputFileEnabled()).thenReturn(false);
        when(createAttributeScoresFilePort.generateFile(attributeValue, maturityLevels)).thenReturn(file);
        when(callAiPromptPort.call(promptArgumentCaptor.capture(), classCaptor.capture())).thenReturn(aiInsight);

        var result = helper.createAttributeAiInsights(attributeInsightsParam).getFirst();
        assertEquals("Insight Content", result.getAiInsight());
        assertEquals(aiInsight.value(), result.getAiInsight());
        assertNotNull(result.getAiInsightTime());
        assertNull(result.getAssessorInsight());
        assertNull(result.getAssessorInsightTime());
        assertNull(result.getAiInputPath());
        assertFalse(result.isApproved());
        assertNotNull(result.getLastModificationTime());
        assertEquals(attributeInsightParam.assessmentResult().getId(), result.getAssessmentResultId());
        assertEquals(expectedPrompt, promptArgumentCaptor.getValue().getContents());

        verifyNoInteractions(uploadAttributeScoresFilePort);
    }

    private AppAiProperties appAiProperties() {
        var properties = new AppAiProperties();
        properties.setEnabled(true);
        properties.setPrompt(new AppAiProperties.Prompt());
        properties.setSaveAiInputFileEnabled(true);
        properties.getPrompt().setAttributeInsight("The attribute {attributeTitle} with this description " +
            "{attributeDescription} for {assessmentTitle} was reviewed in {fileContent}. " +
            "Provide the result in {language}.");
        return properties;
    }

    private AttributeInsightParam createParam(Consumer<AttributeInsightParam.AttributeInsightParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private AttributeInsightParam.AttributeInsightParamBuilder paramBuilder() {
        return AttributeInsightParam.builder()
            .assessmentResult(assessmentResult)
            .attributeId(attributeValue.getAttribute().getId())
            .locale(Locale.of(assessmentResult.getAssessment().getAssessmentKit().getLanguage().getCode()));
    }

    private AttributeInsightsParam createParams(Consumer<AttributeInsightsParam.AttributeInsightsParamBuilder> changer) {
        var paramBuilder = paramsBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private AttributeInsightsParam.AttributeInsightsParamBuilder paramsBuilder() {
        return AttributeInsightsParam.builder()
            .assessmentResult(assessmentResult)
            .attributeIds(List.of(attributeValue.getAttribute().getId()))
            .locale(Locale.of(assessmentResult.getAssessment().getAssessmentKit().getLanguage().getCode()));
    }
}
