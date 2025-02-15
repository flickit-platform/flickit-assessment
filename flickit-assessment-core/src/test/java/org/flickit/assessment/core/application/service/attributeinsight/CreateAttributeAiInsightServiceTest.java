package org.flickit.assessment.core.application.service.attributeinsight;

import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.application.port.out.CallAiPromptPort;
import org.flickit.assessment.common.application.port.out.ValidateAssessmentResultPort;
import org.flickit.assessment.common.config.AppAiProperties;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.CalculateNotValidException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.common.exception.ValidationException;
import org.flickit.assessment.core.application.domain.*;
import org.flickit.assessment.core.application.port.in.attributeinsight.CreateAttributeAiInsightUseCase;
import org.flickit.assessment.core.application.port.in.attributeinsight.CreateAttributeAiInsightUseCase.Param;
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
import org.flickit.assessment.core.test.fixture.application.AttributeValueMother;
import org.flickit.assessment.core.test.fixture.application.MaturityLevelMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.prompt.Prompt;

import java.io.ByteArrayInputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.CREATE_ATTRIBUTE_INSIGHT;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_ASSESSMENT_RESULT_NOT_VALID;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.core.common.ErrorMessageKey.CREATE_ATTRIBUTE_AI_INSIGHT_ALL_QUESTIONS_NOT_ANSWERED;
import static org.flickit.assessment.core.common.ErrorMessageKey.CREATE_ATTRIBUTE_AI_INSIGHT_ASSESSMENT_RESULT_NOT_FOUND;
import static org.flickit.assessment.core.common.MessageKey.ASSESSMENT_AI_IS_DISABLED;
import static org.flickit.assessment.core.test.fixture.application.AssessmentResultMother.invalidResultWithSubjectValues;
import static org.flickit.assessment.core.test.fixture.application.AssessmentResultMother.validResult;
import static org.flickit.assessment.core.test.fixture.application.AttributeInsightMother.aiInsightWithTime;
import static org.flickit.assessment.core.test.fixture.application.AttributeMother.simpleAttribute;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateAttributeAiInsightServiceTest {

    @InjectMocks
    private CreateAttributeAiInsightService service;

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
    private CreateAttributeScoresFilePort generateAttributeValueReportFilePort;

    @Mock
    private UploadAttributeScoresFilePort uploadAttributeScoresFilePort;

    @Mock
    private GetAssessmentProgressPort getAssessmentProgressPort;

    @Captor
    private ArgumentCaptor<AttributeInsight> attributeInsightArgumentCaptor;

    @Captor
    private ArgumentCaptor<Class<String>> classCaptor;

    @Captor
    private ArgumentCaptor<Prompt> promptArgumentCaptor;

    @Spy
    private AppAiProperties appAiProperties = appAiProperties();

    private final Attribute attribute = simpleAttribute();
    private final AssessmentResult assessmentResult = validResult();
    private final String fileContent = "file content";
    private final String fileReportPath = "path/to/file";
    private final String aiInsight = "Insight Content";
    private final CreateAttributeScoresFilePort.Result file = new CreateAttributeScoresFilePort.Result(new ByteArrayInputStream(fileContent.getBytes()), fileContent);
    private final AttributeValue attributeValue = AttributeValueMother.hasFullScoreOnLevel23WithWeight(1, attribute.getId());
    private final List<MaturityLevel> maturityLevels = MaturityLevelMother.allLevels();
    private final CreateAttributeAiInsightUseCase.Param param = createParam(CreateAttributeAiInsightUseCase.Param.ParamBuilder::build);
    private final GetAssessmentProgressPort.Result progress = new GetAssessmentProgressPort.Result(param.getAssessmentId(), 10, 10);

    @Test
    void testCreateAttributeAiInsight_whenCurrentUserDoesNotHaveRequiredPermission_thenThrowAccessDeniedException() {
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
    void testCreateAttributeAiInsight_whenAssessmentProgressIsNotCompleted_thenThrowValidationException() {
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
    void testCreateAttributeAiInsight_whenAssessmentResultIsNotFound_thenThrowResourceNotFoundException() {
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
    void testCreateAttributeAiInsight_whenCalculatedResultIsNotValid_thenThrowCalculateNotValidException() {
        var invalidResult = invalidResultWithSubjectValues(null);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_ATTRIBUTE_INSIGHT)).thenReturn(true);
        when(getAssessmentProgressPort.getProgress(param.getAssessmentId())).thenReturn(progress);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(invalidResult));
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
    void testCreateAttributeAiInsight_whenAiInsightDoesNotExistAndAiEnabled_thenGenerateAndPersistAiInsight() {
        String expectedPrompt = "The attribute " + attribute.getTitle() + " with this description " + attribute.getDescription() +
            " for " + assessmentResult.getAssessment().getShortTitle() + " was reviewed in " + fileContent + ".";

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_ATTRIBUTE_INSIGHT)).thenReturn(true);
        when(getAssessmentProgressPort.getProgress(param.getAssessmentId())).thenReturn(progress);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        when(loadAttributePort.load(attribute.getId(), assessmentResult.getKitVersionId())).thenReturn(attribute);
        when(loadAttributeInsightPort.load(assessmentResult.getId(), param.getAttributeId())).thenReturn(Optional.empty());
        when(callAiPromptPort.call(promptArgumentCaptor.capture(), classCaptor.capture())).thenReturn(aiInsight);
        when(loadAttributeValuePort.load(assessmentResult.getId(), param.getAttributeId())).thenReturn(attributeValue);
        when(loadMaturityLevelsPort.loadByKitVersionId(assessmentResult.getKitVersionId())).thenReturn(maturityLevels);
        when(generateAttributeValueReportFilePort.generateFile(attributeValue, maturityLevels)).thenReturn(file);
        when(uploadAttributeScoresFilePort.uploadExcel(eq(file.stream()), any())).thenReturn(fileReportPath);

        var result = service.createAiInsight(param);
        assertEquals("Insight Content", result.content());
        verify(createAttributeInsightPort).persist(attributeInsightArgumentCaptor.capture());
        assertEquals(aiInsight, attributeInsightArgumentCaptor.getValue().getAiInsight());
        assertNotNull(attributeInsightArgumentCaptor.getValue().getAiInsightTime());
        assertNull(attributeInsightArgumentCaptor.getValue().getAssessorInsight());
        assertNull(attributeInsightArgumentCaptor.getValue().getAssessorInsightTime());
        assertEquals(fileReportPath, attributeInsightArgumentCaptor.getValue().getAiInputPath());
        assertFalse(attributeInsightArgumentCaptor.getValue().isApproved());
        assertNotNull(attributeInsightArgumentCaptor.getValue().getLastModificationTime());
        assertEquals(assessmentResult.getId(), attributeInsightArgumentCaptor.getValue().getAssessmentResultId());
        assertEquals(expectedPrompt, promptArgumentCaptor.getValue().getContents());

        verify(validateAssessmentResultPort).validate(param.getAssessmentId());
        verifyNoInteractions(updateAttributeInsightPort);
    }

    @Test
    void testCreateAttributeAiInsight_whenAiInsightDoesNotExistAndAiEnabledAndSaveFilesDisabled_thenGenerateAndNotSaveFileAndPersistInsight() {
        when(appAiProperties.isSaveAiInputFileEnabled()).thenReturn(false);
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_ATTRIBUTE_INSIGHT)).thenReturn(true);
        when(getAssessmentProgressPort.getProgress(param.getAssessmentId())).thenReturn(progress);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        when(loadAttributePort.load(param.getAttributeId(), assessmentResult.getKitVersionId())).thenReturn(attribute);
        when(loadAttributeInsightPort.load(assessmentResult.getId(), param.getAttributeId())).thenReturn(Optional.empty());
        when(callAiPromptPort.call(promptArgumentCaptor.capture(), classCaptor.capture())).thenReturn(aiInsight);
        when(loadAttributeValuePort.load(assessmentResult.getId(), param.getAttributeId())).thenReturn(attributeValue);
        when(loadMaturityLevelsPort.loadByKitVersionId(assessmentResult.getKitVersionId())).thenReturn(maturityLevels);
        when(generateAttributeValueReportFilePort.generateFile(attributeValue, maturityLevels)).thenReturn(file);

        var result = service.createAiInsight(param);
        verify(createAttributeInsightPort).persist(attributeInsightArgumentCaptor.capture());
        assertEquals("Insight Content", result.content());
        assertEquals(aiInsight, attributeInsightArgumentCaptor.getValue().getAiInsight());
        assertNotNull(attributeInsightArgumentCaptor.getValue().getAiInsightTime());
        assertNull(attributeInsightArgumentCaptor.getValue().getAssessorInsight());
        assertNull(attributeInsightArgumentCaptor.getValue().getAssessorInsightTime());
        assertNull(attributeInsightArgumentCaptor.getValue().getAiInputPath());
        assertFalse(attributeInsightArgumentCaptor.getValue().isApproved());
        assertNotNull(attributeInsightArgumentCaptor.getValue().getLastModificationTime());
        assertEquals(assessmentResult.getId(), attributeInsightArgumentCaptor.getValue().getAssessmentResultId());

        verify(validateAssessmentResultPort).validate(param.getAssessmentId());
        verifyNoInteractions(updateAttributeInsightPort, uploadAttributeScoresFilePort);
    }

    @Test
    void testCreateAttributeAiInsight_whenAiInsightDoesNotExistAndAiDisabled_thenThrowUnsupportedOperationException() {
        when(appAiProperties.isEnabled()).thenReturn(false);
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_ATTRIBUTE_INSIGHT)).thenReturn(true);
        when(getAssessmentProgressPort.getProgress(param.getAssessmentId())).thenReturn(progress);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        when(loadAttributePort.load(attribute.getId(), assessmentResult.getKitVersionId())).thenReturn(attribute);
        when(loadAttributeInsightPort.load(assessmentResult.getId(), param.getAttributeId())).thenReturn(Optional.empty());

        var throwable = assertThrows(UnsupportedOperationException.class, () -> service.createAiInsight(param));
        assertEquals(ASSESSMENT_AI_IS_DISABLED, throwable.getMessage());

        verifyNoInteractions(updateAttributeInsightPort,
            callAiPromptPort,
            createAttributeInsightPort,
            generateAttributeValueReportFilePort,
            uploadAttributeScoresFilePort,
            loadAttributeValuePort,
            loadMaturityLevelsPort);
    }

    @Test
    void testCreateAttributeAiInsight_whenAiInsightExistsAndInsightTimeIsAfterCalculationTime_thenReturnExistingInsight() {
        var attributeInsight = aiInsightWithTime(LocalDateTime.now().plusDays(1));

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_ATTRIBUTE_INSIGHT)).thenReturn(true);
        when(getAssessmentProgressPort.getProgress(param.getAssessmentId())).thenReturn(progress);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        when(loadAttributePort.load(attribute.getId(), assessmentResult.getKitVersionId())).thenReturn(attribute);
        when(loadAttributeInsightPort.load(assessmentResult.getId(), param.getAttributeId())).thenReturn(Optional.of(attributeInsight));

        var result = service.createAiInsight(param);
        assertEquals(result.content(), attributeInsight.getAiInsight());

        ArgumentCaptor<UpdateAttributeInsightPort.AiTimeParam> attributeInsightParam = ArgumentCaptor.forClass(UpdateAttributeInsightPort.AiTimeParam.class);
        verify(updateAttributeInsightPort, times(1)).updateAiInsightTime(attributeInsightParam.capture());
        assertEquals(assessmentResult.getId(), attributeInsightParam.getValue().assessmentResultId());
        assertEquals(param.getAttributeId(), attributeInsightParam.getValue().attributeId());
        assertNotNull(attributeInsightParam.getValue().aiInsightTime());
        assertNotNull(attributeInsightParam.getValue().lastModificationTime());

        verifyNoInteractions(callAiPromptPort,
            generateAttributeValueReportFilePort,
            uploadAttributeScoresFilePort);
    }

    @Test
    void testCreateAttributeAiInsight_whenAiInsightExistsAndInsightTimeIsBeforeCalculationTime_AiEnabled_thenRegenerateAndUpdateInsight() {
        var attributeInsight = aiInsightWithTime(LocalDateTime.now().minusDays(1));

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_ATTRIBUTE_INSIGHT)).thenReturn(true);
        when(getAssessmentProgressPort.getProgress(param.getAssessmentId())).thenReturn(progress);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        when(loadAttributePort.load(attribute.getId(), assessmentResult.getKitVersionId())).thenReturn(attribute);
        when(loadAttributeInsightPort.load(assessmentResult.getId(), param.getAttributeId())).thenReturn(Optional.of(attributeInsight));
        when(callAiPromptPort.call(promptArgumentCaptor.capture(), classCaptor.capture())).thenReturn(attributeInsight.getAiInsight());
        when(loadAttributeValuePort.load(assessmentResult.getId(), param.getAttributeId())).thenReturn(attributeValue);
        when(loadMaturityLevelsPort.loadByKitVersionId(assessmentResult.getKitVersionId())).thenReturn(maturityLevels);
        when(generateAttributeValueReportFilePort.generateFile(attributeValue, maturityLevels)).thenReturn(file);
        when(uploadAttributeScoresFilePort.uploadExcel(eq(file.stream()), any())).thenReturn(fileReportPath);

        var result = service.createAiInsight(param);
        assertEquals(attributeInsight.getAiInsight(), result.content());

        ArgumentCaptor<UpdateAttributeInsightPort.AiParam> captor = ArgumentCaptor.forClass(UpdateAttributeInsightPort.AiParam.class);
        verify(updateAttributeInsightPort).updateAiInsight(captor.capture());
        assertEquals(assessmentResult.getId(), captor.getValue().assessmentResultId());
        assertEquals(param.getAttributeId(), captor.getValue().attributeId());
        assertEquals(attributeInsight.getAiInsight(), captor.getValue().aiInsight());
        assertNotNull(captor.getValue().aiInsightTime());
        assertFalse(captor.getValue().isApproved());
        assertNotNull(captor.getValue().lastModificationTime());

        verify(validateAssessmentResultPort).validate(param.getAssessmentId());
        verifyNoInteractions(createAttributeInsightPort);
    }

    @Test
    void testCreateAttributeAiInsight_whenAiInsightExistsAndInsightTimeIsBeforeCalculationTime_AiEnabledSaveFilesDisabled_thenRegenerateAndNotSaveFileAndUpdateInsight() {
        var attributeInsight = aiInsightWithTime(LocalDateTime.now().minusDays(1));

        when(appAiProperties.isSaveAiInputFileEnabled()).thenReturn(false);
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_ATTRIBUTE_INSIGHT)).thenReturn(true);
        when(getAssessmentProgressPort.getProgress(param.getAssessmentId())).thenReturn(progress);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        when(loadAttributePort.load(attribute.getId(), assessmentResult.getKitVersionId())).thenReturn(attribute);
        when(loadAttributeInsightPort.load(assessmentResult.getId(), param.getAttributeId())).thenReturn(Optional.of(attributeInsight));
        when(callAiPromptPort.call(promptArgumentCaptor.capture(), classCaptor.capture())).thenReturn(aiInsight);
        when(loadAttributeValuePort.load(assessmentResult.getId(), param.getAttributeId())).thenReturn(attributeValue);
        when(loadMaturityLevelsPort.loadByKitVersionId(assessmentResult.getKitVersionId())).thenReturn(maturityLevels);
        when(generateAttributeValueReportFilePort.generateFile(attributeValue, maturityLevels)).thenReturn(file);

        var result = service.createAiInsight(param);
        assertEquals(aiInsight, result.content());
        ArgumentCaptor<UpdateAttributeInsightPort.AiParam> captor = ArgumentCaptor.forClass(UpdateAttributeInsightPort.AiParam.class);
        verify(updateAttributeInsightPort).updateAiInsight(captor.capture());
        assertEquals(assessmentResult.getId(), captor.getValue().assessmentResultId());
        assertEquals(param.getAttributeId(), captor.getValue().attributeId());
        assertNotNull(captor.getValue().aiInsightTime());
        assertFalse(captor.getValue().isApproved());
        assertNotNull(captor.getValue().lastModificationTime());

        verify(validateAssessmentResultPort).validate(param.getAssessmentId());
        verifyNoInteractions(createAttributeInsightPort, uploadAttributeScoresFilePort);
    }

    @Test
    void testCreateAttributeAiInsight_whenAiInsightExistsAndInsightTimeIsBeforeCalculationTime_AiDisabled_thenThrowUnsupportedOperationException() {
        var attributeInsight = aiInsightWithTime(LocalDateTime.now().minusDays(1));

        when(appAiProperties.isEnabled()).thenReturn(false);
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_ATTRIBUTE_INSIGHT)).thenReturn(true);
        when(getAssessmentProgressPort.getProgress(param.getAssessmentId())).thenReturn(progress);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        when(loadAttributePort.load(attribute.getId(), assessmentResult.getKitVersionId())).thenReturn(attribute);
        when(loadAttributeInsightPort.load(assessmentResult.getId(), param.getAttributeId())).thenReturn(Optional.of(attributeInsight));

        var throwable = assertThrows(UnsupportedOperationException.class, () -> service.createAiInsight(param));
        assertEquals(ASSESSMENT_AI_IS_DISABLED, throwable.getMessage());

        verifyNoInteractions(callAiPromptPort,
            createAttributeInsightPort,
            updateAttributeInsightPort,
            generateAttributeValueReportFilePort,
            uploadAttributeScoresFilePort);
    }

    private CreateAttributeAiInsightUseCase.Param createParam(Consumer<Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private CreateAttributeAiInsightUseCase.Param.ParamBuilder paramBuilder() {
        return CreateAttributeAiInsightUseCase.Param.builder()
            .assessmentId(assessmentResult.getAssessment().getId())
            .attributeId(attribute.getId())
            .currentUserId(UUID.randomUUID());
    }

    private AppAiProperties appAiProperties() {
        var properties = new AppAiProperties();
        properties.setEnabled(true);
        properties.setPrompt(new AppAiProperties.Prompt());
        properties.setSaveAiInputFileEnabled(true);
        properties.getPrompt().setAttributeInsight("The attribute {attributeTitle} " +
            "with this description {attributeDescription} for {assessmentTitle} was reviewed in {fileContent}.");
        return properties;
    }
}
