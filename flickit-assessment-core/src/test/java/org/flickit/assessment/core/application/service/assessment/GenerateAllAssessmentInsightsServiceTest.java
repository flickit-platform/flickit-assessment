package org.flickit.assessment.core.application.service.assessment;

import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.application.domain.kit.KitLanguage;
import org.flickit.assessment.common.application.port.out.CallAiPromptPort;
import org.flickit.assessment.common.application.port.out.ValidateAssessmentResultPort;
import org.flickit.assessment.common.config.AppAiProperties;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.CalculateNotValidException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.common.exception.ValidationException;
import org.flickit.assessment.core.application.domain.AssessmentResult;
import org.flickit.assessment.core.application.domain.AttributeInsight;
import org.flickit.assessment.core.application.domain.AttributeValue;
import org.flickit.assessment.core.application.domain.MaturityLevel;
import org.flickit.assessment.core.application.port.in.assessment.GenerateAllAssessmentInsightsUseCase.Param;
import org.flickit.assessment.core.application.port.out.assessment.GetAssessmentProgressPort;
import org.flickit.assessment.core.application.port.out.assessmentinsight.CreateAssessmentInsightPort;
import org.flickit.assessment.core.application.port.out.assessmentinsight.LoadAssessmentInsightPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.core.application.port.out.attribute.CreateAttributeScoresFilePort;
import org.flickit.assessment.core.application.port.out.attribute.LoadAttributesPort;
import org.flickit.assessment.core.application.port.out.attributeinsight.CreateAttributeInsightPort;
import org.flickit.assessment.core.application.port.out.attributeinsight.LoadAttributeInsightsPort;
import org.flickit.assessment.core.application.port.out.attributevalue.LoadAttributeValuePort;
import org.flickit.assessment.core.application.port.out.maturitylevel.LoadMaturityLevelsPort;
import org.flickit.assessment.core.application.port.out.minio.UploadAttributeScoresFilePort;
import org.flickit.assessment.core.application.port.out.subject.LoadSubjectsPort;
import org.flickit.assessment.core.application.port.out.subjectinsight.CreateSubjectInsightPort;
import org.flickit.assessment.core.application.port.out.subjectinsight.LoadSubjectInsightsPort;
import org.flickit.assessment.core.application.port.out.subjectvalue.LoadSubjectValuePort;
import org.flickit.assessment.core.test.fixture.application.AssessmentInsightMother;
import org.flickit.assessment.core.test.fixture.application.AssessmentResultMother;
import org.flickit.assessment.core.test.fixture.application.AttributeValueMother;
import org.flickit.assessment.core.test.fixture.application.MaturityLevelMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.prompt.Prompt;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.GENERATE_ALL_ASSESSMENT_INSIGHTS;
import static org.flickit.assessment.common.error.ErrorMessageKey.*;
import static org.flickit.assessment.core.common.ErrorMessageKey.GENERATE_ALL_ASSESSMENT_INSIGHTS_ALL_QUESTIONS_NOT_ANSWERED;
import static org.flickit.assessment.core.common.MessageKey.ASSESSMENT_AI_IS_DISABLED;
import static org.flickit.assessment.core.test.fixture.application.AssessmentResultMother.validResult;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GenerateAllAssessmentInsightsServiceTest {

    @InjectMocks
    private GenerateAllAssessmentInsightsService service;

    @Mock
    private AssessmentAccessChecker assessmentAccessChecker;

    @Mock
    private ValidateAssessmentResultPort validateAssessmentResultPort;

    @Mock
    private LoadMaturityLevelsPort loadMaturityLevelsPort;

    @Mock
    private GetAssessmentProgressPort getAssessmentProgressPort;

    @Mock
    private LoadAssessmentResultPort loadAssessmentResultPort;

    @Mock
    private LoadAttributesPort loadAttributesPort;

    @Mock
    private LoadAttributeInsightsPort loadAttributeInsightsPort;

    @Spy
    private AppAiProperties appAiProperties;

    @Mock
    private LoadAttributeValuePort loadAttributeValuePort;

    @Mock
    private CreateAttributeScoresFilePort createAttributeScoresFilePort;

    @Mock
    private CallAiPromptPort callAiPromptPort;

    @Mock
    private UploadAttributeScoresFilePort uploadAttributeScoresFilePort;

    @Mock
    private CreateAttributeInsightPort createAttributeInsightPort;

    @Mock
    private LoadSubjectsPort loadSubjectsPort;

    @Mock
    private LoadSubjectInsightsPort loadSubjectInsightsPort;

    @Mock
    private LoadSubjectValuePort loadSubjectValuePort;

    @Mock
    private CreateSubjectInsightPort createSubjectInsightPort;

    @Mock
    private LoadAssessmentInsightPort loadAssessmentInsightPort;

    @Mock
    private CreateAssessmentInsightPort createAssessmentInsightPort;

    @Captor
    private ArgumentCaptor<List<AttributeInsight>> attributeInsightArgumentCaptor;

    @Captor
    private ArgumentCaptor<Class<GenerateAllAssessmentInsightsService.AiResponseDto>> classCaptor;

    @Captor
    private ArgumentCaptor<Prompt> promptArgumentCaptor;

    private final Param param = createParam(Param.ParamBuilder::build);
    private final AssessmentResult assessmentResult = validResult();
    private final LoadAttributesPort.Result attribute = createAttribute(13);
    private final String fileContent = "file content";
    private final GenerateAllAssessmentInsightsService.AiResponseDto aiInsight = new GenerateAllAssessmentInsightsService.AiResponseDto("Insight Content");
    private final CreateAttributeScoresFilePort.Result file = new CreateAttributeScoresFilePort.Result(new ByteArrayInputStream(fileContent.getBytes()), fileContent);
    private final AttributeValue attributeValue = AttributeValueMother.hasFullScoreOnLevel23WithWeight(1, attribute.id());
    private final List<MaturityLevel> maturityLevels = MaturityLevelMother.allLevels();
    private final GetAssessmentProgressPort.Result progress = new GetAssessmentProgressPort.Result(param.getAssessmentId(), 10, 10);

    @Test
    void testGenerateAllAssessmentInsights_whenCurrentUserDoesNotHaveRequiredPermission_thenThrowAccessDeniedException() {
        var param = createParam(Param.ParamBuilder::build);
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), GENERATE_ALL_ASSESSMENT_INSIGHTS)).thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.generateAllAssessmentInsights(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verifyNoInteractions(validateAssessmentResultPort,
            loadAssessmentResultPort,
            loadAttributesPort,
            loadAttributeInsightsPort,
            appAiProperties,
            loadAttributeValuePort,
            createAttributeScoresFilePort,
            callAiPromptPort,
            uploadAttributeScoresFilePort,
            createAttributeInsightPort,
            loadSubjectsPort,
            loadSubjectInsightsPort,
            loadMaturityLevelsPort,
            loadSubjectValuePort,
            createSubjectInsightPort,
            loadAssessmentInsightPort,
            getAssessmentProgressPort,
            createAssessmentInsightPort);
    }

    @Test
    void testGenerateAllAssessmentInsights_whenAssessmentResultIsNotFound_thenThrowResourceNotFoundException() {
        var param = createParam(Param.ParamBuilder::build);
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), GENERATE_ALL_ASSESSMENT_INSIGHTS))
            .thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId()))
            .thenReturn(Optional.empty());

        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.generateAllAssessmentInsights(param));
        assertEquals(COMMON_ASSESSMENT_RESULT_NOT_FOUND, throwable.getMessage());

        verifyNoInteractions(validateAssessmentResultPort,
            loadMaturityLevelsPort,
            getAssessmentProgressPort,
            loadAttributesPort,
            loadAttributeInsightsPort,
            appAiProperties,
            loadAttributeValuePort,
            createAttributeScoresFilePort,
            callAiPromptPort,
            uploadAttributeScoresFilePort,
            createAttributeInsightPort,
            loadSubjectsPort,
            loadSubjectInsightsPort,
            loadSubjectValuePort,
            createSubjectInsightPort,
            loadAssessmentInsightPort,
            createAssessmentInsightPort);
    }

    @Test
    void testGenerateAllAssessmentInsights_whenCalculatedResultIsNotValid_thenThrowCalculateNotValidException() {
        var param = createParam(Param.ParamBuilder::build);
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), GENERATE_ALL_ASSESSMENT_INSIGHTS))
            .thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId()))
            .thenReturn(Optional.of(assessmentResult));
        doThrow(new CalculateNotValidException(COMMON_ASSESSMENT_RESULT_NOT_VALID))
            .when(validateAssessmentResultPort).validate(param.getAssessmentId());

        var throwable = assertThrows(CalculateNotValidException.class, () -> service.generateAllAssessmentInsights(param));
        assertEquals(COMMON_ASSESSMENT_RESULT_NOT_VALID, throwable.getMessage());

        verifyNoInteractions(loadMaturityLevelsPort,
            getAssessmentProgressPort,
            loadAttributesPort,
            loadAttributeInsightsPort,
            appAiProperties,
            loadAttributeValuePort,
            createAttributeScoresFilePort,
            callAiPromptPort,
            uploadAttributeScoresFilePort,
            createAttributeInsightPort,
            loadSubjectsPort,
            loadSubjectInsightsPort,
            loadSubjectValuePort,
            createSubjectInsightPort,
            loadAssessmentInsightPort,
            createAssessmentInsightPort);
    }

    @Test
    void testGenerateAllAssessmentInsights_whenOneAttributeInsightDoesNotExistAndAssessmentProgressIsNotCompleted_thenThrowValidationException() {
        var param = createParam(Param.ParamBuilder::build);
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), GENERATE_ALL_ASSESSMENT_INSIGHTS))
            .thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId()))
            .thenReturn(Optional.of(assessmentResult));
        doNothing().when(validateAssessmentResultPort).validate(param.getAssessmentId());
        when(loadMaturityLevelsPort.loadByKitVersionId(assessmentResult.getKitVersionId()))
            .thenReturn(MaturityLevelMother.allLevels());
        var incompleteProgress = new GetAssessmentProgressPort.Result(param.getAssessmentId(), 10, 11);
        when(getAssessmentProgressPort.getProgress(param.getAssessmentId())).thenReturn(incompleteProgress);
        when(loadAttributesPort.loadAll(param.getAssessmentId())).thenReturn(List.of(attribute));
        when(loadAttributeInsightsPort.loadInsights(assessmentResult.getId())).thenReturn(List.of());

        var throwable = assertThrows(ValidationException.class, () -> service.generateAllAssessmentInsights(param));
        assertEquals(GENERATE_ALL_ASSESSMENT_INSIGHTS_ALL_QUESTIONS_NOT_ANSWERED, throwable.getMessageKey());

        verifyNoInteractions(loadAttributeValuePort,
            createAttributeScoresFilePort,
            callAiPromptPort,
            uploadAttributeScoresFilePort,
            createAttributeInsightPort,
            loadSubjectsPort,
            loadSubjectInsightsPort,
            loadSubjectValuePort,
            createSubjectInsightPort,
            loadAssessmentInsightPort,
            createAssessmentInsightPort);
    }

    @Test
    void testGenerateAllAssessmentInsights_whenOneAttributeInsightDoesNotExistAndAiDisabled_thenThrowUnsupportedOperationException() {
        var param = createParam(Param.ParamBuilder::build);
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), GENERATE_ALL_ASSESSMENT_INSIGHTS))
            .thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId()))
            .thenReturn(Optional.of(assessmentResult));
        doNothing().when(validateAssessmentResultPort).validate(param.getAssessmentId());
        when(loadMaturityLevelsPort.loadByKitVersionId(assessmentResult.getKitVersionId()))
            .thenReturn(MaturityLevelMother.allLevels());
        when(getAssessmentProgressPort.getProgress(param.getAssessmentId())).thenReturn(progress);
        when(loadAttributesPort.loadAll(param.getAssessmentId())).thenReturn(List.of(attribute));
        when(loadAttributeInsightsPort.loadInsights(assessmentResult.getId())).thenReturn(List.of());
        when(appAiProperties.isEnabled()).thenReturn(false);

        var throwable = assertThrows(UnsupportedOperationException.class, () -> service.generateAllAssessmentInsights(param));
        assertEquals(ASSESSMENT_AI_IS_DISABLED, throwable.getMessage());

        verifyNoInteractions(loadAttributeValuePort,
            createAttributeScoresFilePort,
            callAiPromptPort,
            uploadAttributeScoresFilePort,
            createAttributeInsightPort,
            loadSubjectsPort,
            loadSubjectInsightsPort,
            loadSubjectValuePort,
            createSubjectInsightPort,
            loadAssessmentInsightPort,
            createAssessmentInsightPort);
    }

    @Test
    void testGenerateAllAssessmentInsights_whenAiInsightDoesNotExistAndAiEnabledAndSaveFilesDisabled_thenGenerateAndNotSaveFileAndPersistInsight() {
        var assessmentResultWithPersianKit = AssessmentResultMother.validResultWithKitLanguage(KitLanguage.FA);
        var attributePromptTemplate = "The attribute {attributeTitle} with this description {attributeDescription} " +
            "for {assessmentTitle} was reviewed in {fileContent}. " +
            "Provide the result in {language}.";
        var expectedPrompt = "The attribute " + attribute.title() + " with this description " + attribute.description() +
            " for " + assessmentResultWithPersianKit.getAssessment().getShortTitle() + " was reviewed in " + fileContent + ". " +
            "Provide the result in " + assessmentResultWithPersianKit.getAssessment().getAssessmentKit().getLanguage().getTitle() + ".";

        AppAiProperties.Prompt prompt = mock(AppAiProperties.Prompt.class);
        when(prompt.getAttributeInsight()).thenReturn(attributePromptTemplate);
        when(appAiProperties.getPrompt()).thenReturn(prompt);

        var param = createParam(Param.ParamBuilder::build);
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), GENERATE_ALL_ASSESSMENT_INSIGHTS))
            .thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId()))
            .thenReturn(Optional.of(assessmentResultWithPersianKit));
        doNothing().when(validateAssessmentResultPort).validate(param.getAssessmentId());
        when(loadMaturityLevelsPort.loadByKitVersionId(assessmentResultWithPersianKit.getKitVersionId()))
            .thenReturn(maturityLevels);
        var completeProgress = new GetAssessmentProgressPort.Result(param.getAssessmentId(), 11, 11);
        when(getAssessmentProgressPort.getProgress(param.getAssessmentId())).thenReturn(completeProgress);
        when(loadAttributesPort.loadAll(param.getAssessmentId())).thenReturn(List.of(attribute));
        when(loadAttributeInsightsPort.loadInsights(assessmentResultWithPersianKit.getId())).thenReturn(List.of());
        when(appAiProperties.isEnabled()).thenReturn(true);
        when(appAiProperties.isSaveAiInputFileEnabled()).thenReturn(false);

        when(loadAttributeValuePort.load(assessmentResultWithPersianKit.getId(), attribute.id())).thenReturn(attributeValue);
        when(createAttributeScoresFilePort.generateFile(attributeValue, maturityLevels)).thenReturn(file);
        when(callAiPromptPort.call(promptArgumentCaptor.capture(), classCaptor.capture())).thenReturn(aiInsight);

        when(loadSubjectsPort.loadByKitVersionIdWithAttributes(assessmentResultWithPersianKit.getKitVersionId())).thenReturn(List.of());
        when(loadSubjectsPort.loadByKitVersionIdWithAttributes(assessmentResultWithPersianKit.getKitVersionId())).thenReturn(List.of());
        when(loadSubjectInsightsPort.loadSubjectInsights(assessmentResultWithPersianKit.getId())).thenReturn(List.of());
        when(loadAssessmentInsightPort.loadByAssessmentResultId(assessmentResultWithPersianKit.getId()))
            .thenReturn(Optional.of(AssessmentInsightMother.createSimpleAssessmentInsight()));

        service.generateAllAssessmentInsights(param);
        verify(createAttributeInsightPort).persistAll(attributeInsightArgumentCaptor.capture());
        assertEquals(aiInsight.value(), attributeInsightArgumentCaptor.getValue().getFirst().getAiInsight());
        assertNotNull(attributeInsightArgumentCaptor.getValue().getFirst().getAiInsightTime());
        assertNull(attributeInsightArgumentCaptor.getValue().getFirst().getAssessorInsight());
        assertNull(attributeInsightArgumentCaptor.getValue().getFirst().getAssessorInsightTime());
        assertNull(attributeInsightArgumentCaptor.getValue().getFirst().getAiInputPath());
        assertFalse(attributeInsightArgumentCaptor.getValue().getFirst().isApproved());
        assertNotNull(attributeInsightArgumentCaptor.getValue().getFirst().getLastModificationTime());
        assertEquals(assessmentResultWithPersianKit.getId(), attributeInsightArgumentCaptor.getValue().getFirst().getAssessmentResultId());
        assertEquals(expectedPrompt, promptArgumentCaptor.getValue().getContents());

        verify(validateAssessmentResultPort).validate(param.getAssessmentId());
        verifyNoInteractions(uploadAttributeScoresFilePort,
            loadSubjectValuePort,
            createSubjectInsightPort,
            createAssessmentInsightPort);
    }

    private static LoadAttributesPort.Result createAttribute(int index) {
        return new LoadAttributesPort.Result(1769L + index,
            "Software Reliability" + index,
            "How?",
            index,
            3 + index,
            11.22 + index,
            new LoadAttributesPort.MaturityLevel(1991L + index,
                "Unprepared" + index,
                "causing frequent issues and inefficiencies." + index, 4, 4),
            new LoadAttributesPort.Subject(464L + index, "Software" + index));
    }

    private Param createParam(Consumer<Param.ParamBuilder> changer) {
        var param = paramBuilder();
        changer.accept(param);
        return param.build();
    }

    private Param.ParamBuilder paramBuilder() {
        return Param.builder()
            .assessmentId(UUID.randomUUID())
            .currentUserId(UUID.randomUUID());
    }

}