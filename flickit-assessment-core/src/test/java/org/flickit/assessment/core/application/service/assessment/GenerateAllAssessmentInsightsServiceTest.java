package org.flickit.assessment.core.application.service.assessment;

import org.flickit.assessment.common.application.MessageBundle;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.application.domain.kit.KitLanguage;
import org.flickit.assessment.common.application.port.out.CallAiPromptPort;
import org.flickit.assessment.common.application.port.out.ValidateAssessmentResultPort;
import org.flickit.assessment.common.config.AppAiProperties;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.CalculateNotValidException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.common.exception.ValidationException;
import org.flickit.assessment.core.application.domain.*;
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
import org.flickit.assessment.core.test.fixture.application.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.prompt.Prompt;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.GENERATE_ALL_ASSESSMENT_INSIGHTS;
import static org.flickit.assessment.common.error.ErrorMessageKey.*;
import static org.flickit.assessment.core.common.ErrorMessageKey.GENERATE_ALL_ASSESSMENT_INSIGHTS_ALL_QUESTIONS_NOT_ANSWERED;
import static org.flickit.assessment.core.common.MessageKey.*;
import static org.flickit.assessment.core.test.fixture.application.MaturityLevelMother.levelFour;
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
    private ArgumentCaptor<List<SubjectInsight>> subjectInsightArgumentCaptor;

    @Captor
    private ArgumentCaptor<Class<GenerateAllAssessmentInsightsService.AiResponseDto>> classCaptor;

    @Captor
    private ArgumentCaptor<Prompt> promptArgumentCaptor;

    private final Param param = createParam(Param.ParamBuilder::build);
    private final AssessmentResult assessmentResult = AssessmentResultMother.validResultWithKitLanguage(KitLanguage.FA);
    private final LoadAttributesPort.Result attribute = createAttribute();
    private final String fileContent = "file content";
    private final GenerateAllAssessmentInsightsService.AiResponseDto aiInsight = new GenerateAllAssessmentInsightsService.AiResponseDto("Insight Content");
    private final CreateAttributeScoresFilePort.Result file = new CreateAttributeScoresFilePort.Result(new ByteArrayInputStream(fileContent.getBytes()), fileContent);
    private final AttributeValue attributeValue = AttributeValueMother.hasFullScoreOnLevel23WithWeight(1, attribute.id());
    private final List<MaturityLevel> maturityLevels = MaturityLevelMother.allLevels();
    private final GetAssessmentProgressPort.Result progress = new GetAssessmentProgressPort.Result(param.getAssessmentId(), 10, 10);
    private final SubjectValue subjectValue = SubjectValueMother.createSubjectValue();

    @Test
    void testGenerateAllAssessmentInsights_whenCurrentUserDoesNotHaveRequiredPermission_thenThrowAccessDeniedException() {
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
    void testGenerateAllAssessmentInsights_whenOneAttributeInsightDoesNotExistAndAiEnabledAndSaveFilesDisabled_thenGenerateAndNotSaveFileAndPersistInsight() {
        var attributePromptTemplate = "The attribute {attributeTitle} with this description {attributeDescription} " +
            "for {assessmentTitle} was reviewed in {fileContent}. Provide the result in {language}.";
        var expectedPrompt = "The attribute " + attributeValue.getAttribute().getTitle() + " with this description " +
            attributeValue.getAttribute().getDescription() + " for " +
            assessmentResult.getAssessment().getShortTitle() + " was reviewed in " + fileContent + ". " +
            "Provide the result in " +
            assessmentResult.getAssessment().getAssessmentKit().getLanguage().getTitle() + ".";
        var prompt = mock(AppAiProperties.Prompt.class);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), GENERATE_ALL_ASSESSMENT_INSIGHTS))
            .thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId()))
            .thenReturn(Optional.of(assessmentResult));
        doNothing().when(validateAssessmentResultPort).validate(param.getAssessmentId());
        when(loadMaturityLevelsPort.loadByKitVersionId(assessmentResult.getKitVersionId()))
            .thenReturn(maturityLevels);
        when(getAssessmentProgressPort.getProgress(param.getAssessmentId())).thenReturn(progress);
        when(loadAttributesPort.loadAll(param.getAssessmentId())).thenReturn(List.of(attribute));
        when(loadAttributeInsightsPort.loadInsights(assessmentResult.getId())).thenReturn(List.of());
        when(appAiProperties.isEnabled()).thenReturn(true);
        when(appAiProperties.isSaveAiInputFileEnabled()).thenReturn(false);

        when(loadAttributeValuePort.load(assessmentResult.getId(), attribute.id())).thenReturn(attributeValue);
        when(createAttributeScoresFilePort.generateFile(attributeValue, maturityLevels)).thenReturn(file);
        when(prompt.getAttributeInsight()).thenReturn(attributePromptTemplate);
        when(appAiProperties.getPrompt()).thenReturn(prompt);
        when(callAiPromptPort.call(promptArgumentCaptor.capture(), classCaptor.capture())).thenReturn(aiInsight);

        when(loadSubjectsPort.loadByKitVersionIdWithAttributes(assessmentResult.getKitVersionId()))
            .thenReturn(List.of());
        when(loadSubjectInsightsPort.loadSubjectInsights(assessmentResult.getId())).thenReturn(List.of());
        when(loadAssessmentInsightPort.loadByAssessmentResultId(assessmentResult.getId()))
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
        assertEquals(assessmentResult.getId(), attributeInsightArgumentCaptor.getValue().getFirst().getAssessmentResultId());
        assertEquals(expectedPrompt, promptArgumentCaptor.getValue().getContents());

        verify(validateAssessmentResultPort).validate(param.getAssessmentId());
        verifyNoInteractions(uploadAttributeScoresFilePort,
            loadSubjectValuePort,
            createSubjectInsightPort,
            createAssessmentInsightPort);
    }

    @Test
    void testGenerateAllAssessmentInsights_whenOneAttributeInsightDoesNotExistAndAiEnabledAndSaveFilesEnabled_thenGenerateAndSaveFileAndPersistInsight() {
        var attributePromptTemplate = "The attribute {attributeTitle} with this description {attributeDescription} " +
            "for {assessmentTitle} was reviewed in {fileContent}. Provide the result in {language}.";
        var expectedPrompt = "The attribute " + attributeValue.getAttribute().getTitle() + " with this description " +
            attributeValue.getAttribute().getDescription() + " for " +
            assessmentResult.getAssessment().getShortTitle() + " was reviewed in " + fileContent + ". " +
            "Provide the result in " +
            assessmentResult.getAssessment().getAssessmentKit().getLanguage().getTitle() + ".";

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), GENERATE_ALL_ASSESSMENT_INSIGHTS))
            .thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId()))
            .thenReturn(Optional.of(assessmentResult));
        doNothing().when(validateAssessmentResultPort).validate(param.getAssessmentId());
        when(loadMaturityLevelsPort.loadByKitVersionId(assessmentResult.getKitVersionId()))
            .thenReturn(maturityLevels);
        var prompt = mock(AppAiProperties.Prompt.class);
        var fileReportPath = "path/to/file";
        when(getAssessmentProgressPort.getProgress(param.getAssessmentId())).thenReturn(progress);
        when(loadAttributesPort.loadAll(param.getAssessmentId())).thenReturn(List.of(attribute));
        when(loadAttributeInsightsPort.loadInsights(assessmentResult.getId())).thenReturn(List.of());
        when(appAiProperties.isEnabled()).thenReturn(true);
        when(appAiProperties.isSaveAiInputFileEnabled()).thenReturn(true);

        when(loadAttributeValuePort.load(assessmentResult.getId(), attribute.id())).thenReturn(attributeValue);
        when(createAttributeScoresFilePort.generateFile(attributeValue, maturityLevels)).thenReturn(file);
        when(prompt.getAttributeInsight()).thenReturn(attributePromptTemplate);
        when(appAiProperties.getPrompt()).thenReturn(prompt);
        when(callAiPromptPort.call(promptArgumentCaptor.capture(), classCaptor.capture())).thenReturn(aiInsight);
        when(uploadAttributeScoresFilePort.uploadExcel(eq(file.stream()), any())).thenReturn(fileReportPath);

        when(loadSubjectsPort.loadByKitVersionIdWithAttributes(assessmentResult.getKitVersionId()))
            .thenReturn(List.of());
        when(loadSubjectInsightsPort.loadSubjectInsights(assessmentResult.getId())).thenReturn(List.of());
        when(loadAssessmentInsightPort.loadByAssessmentResultId(assessmentResult.getId()))
            .thenReturn(Optional.of(AssessmentInsightMother.createSimpleAssessmentInsight()));

        service.generateAllAssessmentInsights(param);
        verify(createAttributeInsightPort).persistAll(attributeInsightArgumentCaptor.capture());
        assertEquals(aiInsight.value(), attributeInsightArgumentCaptor.getValue().getFirst().getAiInsight());
        assertNotNull(attributeInsightArgumentCaptor.getValue().getFirst().getAiInsightTime());
        assertEquals(fileReportPath, attributeInsightArgumentCaptor.getValue().getFirst().getAiInputPath());
        assertNull(attributeInsightArgumentCaptor.getValue().getFirst().getAssessorInsight());
        assertNull(attributeInsightArgumentCaptor.getValue().getFirst().getAssessorInsightTime());
        assertFalse(attributeInsightArgumentCaptor.getValue().getFirst().isApproved());
        assertNotNull(attributeInsightArgumentCaptor.getValue().getFirst().getLastModificationTime());
        assertEquals(assessmentResult.getId(), attributeInsightArgumentCaptor.getValue().getFirst().getAssessmentResultId());
        assertEquals(expectedPrompt, promptArgumentCaptor.getValue().getContents());

        verify(validateAssessmentResultPort).validate(param.getAssessmentId());
        verifyNoInteractions(loadSubjectValuePort,
            createSubjectInsightPort,
            createAssessmentInsightPort);
    }

    @Test
    void testGenerateAllAssessmentInsights_whenOneSubjectInsightDoesNotExist_thenInitSubjectInsightAndPersist() {
        var subject = subjectValue.getSubject();
        var insight = createSubjectDefaultInsight(assessmentResult, subjectValue);
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), GENERATE_ALL_ASSESSMENT_INSIGHTS))
            .thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId()))
            .thenReturn(Optional.of(assessmentResult));
        doNothing().when(validateAssessmentResultPort).validate(param.getAssessmentId());
        when(loadMaturityLevelsPort.loadByKitVersionId(assessmentResult.getKitVersionId()))
            .thenReturn(maturityLevels);
        when(getAssessmentProgressPort.getProgress(param.getAssessmentId())).thenReturn(progress);

        when(loadAttributesPort.loadAll(param.getAssessmentId())).thenReturn(List.of(attribute));
        when(loadAttributeInsightsPort.loadInsights(assessmentResult.getId()))
            .thenReturn(List.of(AttributeInsightMother.aiInsightAttributeId(attribute.id())));

        when(loadSubjectsPort.loadByKitVersionIdWithAttributes(assessmentResult.getKitVersionId()))
            .thenReturn(List.of(subject));
        when(loadSubjectInsightsPort.loadSubjectInsights(assessmentResult.getId())).thenReturn(List.of());
        when(loadSubjectValuePort.loadAll(assessmentResult.getId(), List.of(subject.getId())))
            .thenReturn(List.of(subjectValue));
        doNothing().when(createSubjectInsightPort).persistAll(anyList());

        when(loadAssessmentInsightPort.loadByAssessmentResultId(assessmentResult.getId()))
            .thenReturn(Optional.of(AssessmentInsightMother.createSimpleAssessmentInsight()));

        service.generateAllAssessmentInsights(param);

        verify(createSubjectInsightPort, times(1)).persistAll(subjectInsightArgumentCaptor.capture());

        var subjectInsightArgument = subjectInsightArgumentCaptor.getValue().getFirst();
        assertEquals(assessmentResult.getId(), subjectInsightArgument.getAssessmentResultId());
        assertEquals(subject.getId(), subjectInsightArgument.getSubjectId());
        assertEquals(insight, subjectInsightArgument.getInsight());
        assertNotNull(subjectInsightArgument.getInsightTime());
        assertNotNull(subjectInsightArgument.getLastModificationTime());
        assertNull(subjectInsightArgument.getInsightBy());
        assertFalse(subjectInsightArgument.isApproved());

        verify(validateAssessmentResultPort).validate(param.getAssessmentId());
        verifyNoInteractions(appAiProperties,
            loadAttributeValuePort,
            createAttributeScoresFilePort,
            callAiPromptPort,
            uploadAttributeScoresFilePort,
            createAttributeInsightPort,
            createAssessmentInsightPort);
    }

    @Test
    void testGenerateAllAssessmentInsights_whenAssessmentInsightDoesNotExistAndAssessmentProgressIsCompleted_thenInitAssessmentInsightAndPersist() {
        var subject = subjectValue.getSubject();
        var insight = MessageBundle.message(ASSESSMENT_DEFAULT_INSIGHT_DEFAULT_COMPLETED,
            Locale.of(assessmentResult.getAssessment().getAssessmentKit().getLanguage().getCode()),
            levelFour().getTitle(),
            progress.questionsCount(),
            (int) Math.ceil(assessmentResult.getConfidenceValue()));
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), GENERATE_ALL_ASSESSMENT_INSIGHTS))
            .thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId()))
            .thenReturn(Optional.of(assessmentResult));
        doNothing().when(validateAssessmentResultPort).validate(param.getAssessmentId());
        when(loadMaturityLevelsPort.loadByKitVersionId(assessmentResult.getKitVersionId()))
            .thenReturn(maturityLevels);
        when(getAssessmentProgressPort.getProgress(param.getAssessmentId())).thenReturn(progress);

        when(loadAttributesPort.loadAll(param.getAssessmentId())).thenReturn(List.of(attribute));
        when(loadAttributeInsightsPort.loadInsights(assessmentResult.getId()))
            .thenReturn(List.of(AttributeInsightMother.aiInsightAttributeId(attribute.id())));

        when(loadSubjectsPort.loadByKitVersionIdWithAttributes(assessmentResult.getKitVersionId()))
            .thenReturn(List.of(subject));
        when(loadSubjectInsightsPort.loadSubjectInsights(assessmentResult.getId()))
            .thenReturn(List.of(SubjectInsightMother.subjectInsightWithSubjectId(subject.getId())));

        when(loadAssessmentInsightPort.loadByAssessmentResultId(assessmentResult.getId())).thenReturn(Optional.empty());
        when(createAssessmentInsightPort.persist(any(AssessmentInsight.class))).thenReturn(UUID.randomUUID());
        service.generateAllAssessmentInsights(param);

        var assessmentInsightArgumentCaptor = ArgumentCaptor.forClass(AssessmentInsight.class);
        verify(createAssessmentInsightPort, times(1))
            .persist(assessmentInsightArgumentCaptor.capture());

        assertNull(assessmentInsightArgumentCaptor.getValue().getId());
        assertEquals(assessmentResult.getId(), assessmentInsightArgumentCaptor.getValue().getAssessmentResultId());
        assertEquals(insight, assessmentInsightArgumentCaptor.getValue().getInsight());
        assertNotNull(assessmentInsightArgumentCaptor.getValue().getInsightTime());
        assertNotNull(assessmentInsightArgumentCaptor.getValue().getLastModificationTime());
        assertNull(assessmentInsightArgumentCaptor.getValue().getInsightBy());
        assertFalse(assessmentInsightArgumentCaptor.getValue().isApproved());

        verify(validateAssessmentResultPort).validate(param.getAssessmentId());
        verifyNoInteractions(appAiProperties,
            loadAttributeValuePort,
            createAttributeScoresFilePort,
            callAiPromptPort,
            uploadAttributeScoresFilePort,
            createAttributeInsightPort,
            loadSubjectValuePort,
            createSubjectInsightPort);
    }

    @Test
    void testGenerateAllAssessmentInsights_whenAssessmentInsightDoesNotExistAndAssessmentProgressIsNotCompleted_thenInitAssessmentInsightAndPersist() {
        var subject = subjectValue.getSubject();
        var incompleteProgress = new GetAssessmentProgressPort.Result(UUID.randomUUID(), 10, 11);
        var insight = MessageBundle.message(ASSESSMENT_DEFAULT_INSIGHT_DEFAULT_INCOMPLETE,
            Locale.of(assessmentResult.getAssessment().getAssessmentKit().getLanguage().getCode()),
            levelFour().getTitle(),
            incompleteProgress.answersCount(),
            incompleteProgress.questionsCount(),
            (int) Math.ceil(assessmentResult.getConfidenceValue()));
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), GENERATE_ALL_ASSESSMENT_INSIGHTS))
            .thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId()))
            .thenReturn(Optional.of(assessmentResult));
        doNothing().when(validateAssessmentResultPort).validate(param.getAssessmentId());
        when(loadMaturityLevelsPort.loadByKitVersionId(assessmentResult.getKitVersionId()))
            .thenReturn(maturityLevels);
        when(getAssessmentProgressPort.getProgress(param.getAssessmentId())).thenReturn(incompleteProgress);

        when(loadAttributesPort.loadAll(param.getAssessmentId())).thenReturn(List.of(attribute));
        when(loadAttributeInsightsPort.loadInsights(assessmentResult.getId()))
            .thenReturn(List.of(AttributeInsightMother.aiInsightAttributeId(attribute.id())));

        when(loadSubjectsPort.loadByKitVersionIdWithAttributes(assessmentResult.getKitVersionId()))
            .thenReturn(List.of(subject));
        when(loadSubjectInsightsPort.loadSubjectInsights(assessmentResult.getId()))
            .thenReturn(List.of(SubjectInsightMother.subjectInsightWithSubjectId(subject.getId())));

        when(loadAssessmentInsightPort.loadByAssessmentResultId(assessmentResult.getId())).thenReturn(Optional.empty());
        when(createAssessmentInsightPort.persist(any(AssessmentInsight.class))).thenReturn(UUID.randomUUID());
        service.generateAllAssessmentInsights(param);

        var assessmentInsightArgumentCaptor = ArgumentCaptor.forClass(AssessmentInsight.class);
        verify(createAssessmentInsightPort, times(1))
            .persist(assessmentInsightArgumentCaptor.capture());

        assertNull(assessmentInsightArgumentCaptor.getValue().getId());
        assertEquals(assessmentResult.getId(), assessmentInsightArgumentCaptor.getValue().getAssessmentResultId());
        assertEquals(insight, assessmentInsightArgumentCaptor.getValue().getInsight());
        assertNotNull(assessmentInsightArgumentCaptor.getValue().getInsightTime());
        assertNotNull(assessmentInsightArgumentCaptor.getValue().getLastModificationTime());
        assertNull(assessmentInsightArgumentCaptor.getValue().getInsightBy());
        assertFalse(assessmentInsightArgumentCaptor.getValue().isApproved());

        verify(validateAssessmentResultPort).validate(param.getAssessmentId());
        verifyNoInteractions(appAiProperties,
            loadAttributeValuePort,
            createAttributeScoresFilePort,
            callAiPromptPort,
            uploadAttributeScoresFilePort,
            createAttributeInsightPort,
            loadSubjectValuePort,
            createSubjectInsightPort);
    }

    private static LoadAttributesPort.Result createAttribute() {
        return new LoadAttributesPort.Result(1769L,
            "Software Reliability" + 13,
            "How?",
            13,
            3,
            11.22,
            new LoadAttributesPort.MaturityLevel(1991L,
                "Unprepared" + 13,
                "causing frequent issues and inefficiencies. " + 13, 13, 4),
            new LoadAttributesPort.Subject(464L, "Software" + 13));
    }

    private String createSubjectDefaultInsight(AssessmentResult assessmentResult, SubjectValue subjectValue) {
        return MessageBundle.message(SUBJECT_DEFAULT_INSIGHT,
            Locale.of(assessmentResult.getAssessment().getAssessmentKit().getLanguage().getCode()),
            subjectValue.getSubject().getTitle(),
            subjectValue.getSubject().getDescription(),
            (int) Math.ceil(subjectValue.getConfidenceValue()),
            subjectValue.getSubject().getTitle(),
            subjectValue.getMaturityLevel().getIndex(),
            maturityLevels.size(),
            subjectValue.getMaturityLevel().getTitle(),
            subjectValue.getSubject().getAttributes().size(),
            subjectValue.getSubject().getTitle());
    }

    private Param createParam(Consumer<Param.ParamBuilder> changer) {
        var newParam = paramBuilder();
        changer.accept(newParam);
        return newParam.build();
    }

    private Param.ParamBuilder paramBuilder() {
        return Param.builder()
            .assessmentId(UUID.randomUUID())
            .currentUserId(UUID.randomUUID());
    }

}