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
import org.flickit.assessment.core.application.port.out.minio.UploadAttributeScoresFilePort;
import org.flickit.assessment.core.application.service.insight.attribute.CreateAttributeAiInsightHelper.Param;
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
    private final String fileContent = "file content";
    private final CreateAttributeAiInsightHelper.AiResponseDto aiInsight = new CreateAttributeAiInsightHelper.AiResponseDto("Insight Content");
    private final CreateAttributeScoresFilePort.Result file = new CreateAttributeScoresFilePort.Result(new ByteArrayInputStream(fileContent.getBytes()), fileContent);
    private final AttributeValue attributeValue = AttributeValueMother.hasFullScoreOnLevel23WithWeight(1, 159L);
    private final List<MaturityLevel> maturityLevels = MaturityLevelMother.allLevels();
    private final Param param = createParam(Param.ParamBuilder::build);

    @Test
    void testCreateAttributeAiInsightHelper_whenAssessmentProgressIsNotCompleted_thenThrowValidationException() {
        var incompleteProgress = new GetAssessmentProgressPort.Result(param.assessmentResult().getId(), 10, 11);
        var paramWithIncompleteProgress = createParam(b -> b.assessmentProgress(incompleteProgress));

        var throwable = assertThrows(ValidationException.class, () -> helper.createAttributeAiInsight(paramWithIncompleteProgress));
        assertEquals(CREATE_ATTRIBUTE_AI_INSIGHT_ALL_QUESTIONS_NOT_ANSWERED, throwable.getMessageKey());

        verifyNoInteractions(loadAttributeValuePort,
            appAiProperties,
            createAttributeScoresFilePort,
            uploadAttributeScoresFilePort,
            callAiPromptPort);
    }

    @Test
    void testCreateAttributeAiInsightHelper_whenAiDisabled_thenThrowUnsupportedOperationException() {
        when(loadAttributeValuePort.load(param.assessmentResult().getId(), param.attributeId())).thenReturn(attributeValue);
        when(appAiProperties.isEnabled()).thenReturn(false);

        var throwable = assertThrows(UnsupportedOperationException.class, () -> helper.createAttributeAiInsight(param));
        assertEquals(ASSESSMENT_AI_IS_DISABLED, throwable.getMessage());

        verifyNoInteractions(createAttributeScoresFilePort,
            uploadAttributeScoresFilePort,
            callAiPromptPort);
    }

    @Test
    void testCreateAttributeAiInsightHelper_whenAiEnabledAndSaveFileEnabled_thenGenerateAndUploadFile() {
        var expectedPrompt = "The attribute " + attributeValue.getAttribute().getTitle() + " with this description " + attributeValue.getAttribute().getDescription() +
            " for " + param.assessmentResult().getAssessment().getShortTitle() + " was reviewed in " + fileContent + ". " +
            "Provide the result in " + param.assessmentResult().getAssessment().getAssessmentKit().getLanguage().getTitle() + ".";
        var fileReportPath = "path/to/file";

        when(loadAttributeValuePort.load(param.assessmentResult().getId(), param.attributeId())).thenReturn(attributeValue);
        when(createAttributeScoresFilePort.generateFile(attributeValue, maturityLevels)).thenReturn(file);
        when(uploadAttributeScoresFilePort.uploadExcel(eq(file.stream()), any())).thenReturn(fileReportPath);
        when(callAiPromptPort.call(promptArgumentCaptor.capture(), classCaptor.capture())).thenReturn(aiInsight);

        var result = helper.createAttributeAiInsight(param);
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
            attributeValue.getAttribute().getDescription() + " for " + param.assessmentResult().getAssessment().getShortTitle() +
            " was reviewed in " + fileContent + ". " + "Provide the result in " +
            param.assessmentResult().getAssessment().getAssessmentKit().getLanguage().getTitle() + ".";


        when(loadAttributeValuePort.load(param.assessmentResult().getId(), param.attributeId())).thenReturn(attributeValue);
        when(appAiProperties.isSaveAiInputFileEnabled()).thenReturn(false);
        when(createAttributeScoresFilePort.generateFile(attributeValue, maturityLevels)).thenReturn(file);
        when(callAiPromptPort.call(promptArgumentCaptor.capture(), classCaptor.capture())).thenReturn(aiInsight);

        var result = helper.createAttributeAiInsight(param);
        assertEquals("Insight Content", result.getAiInsight());
        assertEquals(aiInsight.value(), result.getAiInsight());
        assertNotNull(result.getAiInsightTime());
        assertNull(result.getAssessorInsight());
        assertNull(result.getAssessorInsightTime());
        assertNull(result.getAiInputPath());
        assertFalse(result.isApproved());
        assertNotNull(result.getLastModificationTime());
        assertEquals(param.assessmentResult().getId(), result.getAssessmentResultId());
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

    private Param createParam(Consumer<Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private Param.ParamBuilder paramBuilder() {
        return Param.builder()
            .assessmentResult(assessmentResult)
            .attributeId(attributeValue.getAttribute().getId())
            .maturityLevels(maturityLevels)
            .assessmentProgress(new GetAssessmentProgressPort.Result(assessmentResult.getId(), 10, 10))
            .locale(Locale.of(assessmentResult.getAssessment().getAssessmentKit().getLanguage().getCode()));
    }
}
