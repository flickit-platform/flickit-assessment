package org.flickit.assessment.core.application.service.insight;

import org.flickit.assessment.common.application.MessageBundle;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.application.domain.kit.KitLanguage;
import org.flickit.assessment.common.application.port.out.ValidateAssessmentResultPort;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.CalculateNotValidException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.*;
import org.flickit.assessment.core.application.port.in.insight.GenerateAllAssessmentInsightsUseCase.Param;
import org.flickit.assessment.core.application.port.out.assessment.GetAssessmentProgressPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.core.application.port.out.attribute.LoadAttributesPort;
import org.flickit.assessment.core.application.port.out.insight.assessmentinsight.CreateAssessmentInsightPort;
import org.flickit.assessment.core.application.port.out.insight.assessmentinsight.LoadAssessmentInsightPort;
import org.flickit.assessment.core.application.port.out.insight.attributeinsight.CreateAttributeInsightPort;
import org.flickit.assessment.core.application.port.out.insight.attributeinsight.LoadAttributeInsightsPort;
import org.flickit.assessment.core.application.port.out.insight.subjectinsight.CreateSubjectInsightPort;
import org.flickit.assessment.core.application.port.out.insight.subjectinsight.LoadSubjectInsightsPort;
import org.flickit.assessment.core.application.port.out.maturitylevel.LoadMaturityLevelsPort;
import org.flickit.assessment.core.application.port.out.subject.LoadSubjectsPort;
import org.flickit.assessment.core.application.port.out.subjectvalue.LoadSubjectValuePort;
import org.flickit.assessment.core.application.service.insight.attributeinsight.CreateAttributeAiInsightHelper;
import org.flickit.assessment.core.test.fixture.application.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.GENERATE_ALL_ASSESSMENT_INSIGHTS;
import static org.flickit.assessment.common.error.ErrorMessageKey.*;
import static org.flickit.assessment.core.common.MessageKey.*;
import static org.flickit.assessment.core.test.fixture.application.AttributeInsightMother.aiInsightWithTime;
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

    @Mock
    private CreateAttributeAiInsightHelper createAttributeAiInsightHelper;

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
    private ArgumentCaptor<CreateAttributeAiInsightHelper.Param> helperParamArgumentCaptor;

    @Captor
    private ArgumentCaptor<List<AttributeInsight>> attributeInsightArgumentCaptor;

    @Captor
    private ArgumentCaptor<List<SubjectInsight>> subjectInsightArgumentCaptor;

    private final Param param = createParam(Param.ParamBuilder::build);
    private final AssessmentResult assessmentResult = AssessmentResultMother.validResultWithKitLanguage(KitLanguage.FA);
    private final LoadAttributesPort.Result attribute = createAttribute();
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
            createAttributeAiInsightHelper,
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
            createAttributeAiInsightHelper,
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
            createAttributeAiInsightHelper,
            createAttributeInsightPort,
            loadSubjectsPort,
            loadSubjectInsightsPort,
            loadSubjectValuePort,
            createSubjectInsightPort,
            loadAssessmentInsightPort,
            createAssessmentInsightPort);
    }

    @Test
    void testGenerateAllAssessmentInsights_whenOneAttributeInsightDoesNotExist_thenCreateAttributeAiInsightAndPersist() {
        var newAttributeAiInsight = aiInsightWithTime(LocalDateTime.now());

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

        when(createAttributeAiInsightHelper.createAttributeAiInsight(helperParamArgumentCaptor.capture()))
            .thenReturn(newAttributeAiInsight);

        when(loadSubjectsPort.loadByKitVersionIdWithAttributes(assessmentResult.getKitVersionId()))
            .thenReturn(List.of());
        when(loadSubjectInsightsPort.loadSubjectInsights(assessmentResult.getId())).thenReturn(List.of());
        when(loadAssessmentInsightPort.loadByAssessmentResultId(assessmentResult.getId()))
            .thenReturn(Optional.of(AssessmentInsightMother.createSimpleAssessmentInsight()));

        service.generateAllAssessmentInsights(param);

        assertEquals(assessmentResult, helperParamArgumentCaptor.getValue().assessmentResult());
        assertEquals(attribute.id(), helperParamArgumentCaptor.getValue().attributeId());
        assertEquals(maturityLevels, helperParamArgumentCaptor.getValue().maturityLevels());
        assertEquals(progress, helperParamArgumentCaptor.getValue().assessmentProgress());
        assertEquals(Locale.of(assessmentResult.getAssessment().getAssessmentKit().getLanguage().getCode()),
            helperParamArgumentCaptor.getValue().locale());

        verify(createAttributeInsightPort, times(1)).persistAll(attributeInsightArgumentCaptor.capture());
        assertEquals(newAttributeAiInsight, attributeInsightArgumentCaptor.getValue().getFirst());

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
            .thenReturn(List.of(AttributeInsightMother.aiInsightWithAttributeId(attribute.id())));

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
        verifyNoInteractions(createAttributeAiInsightHelper,
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
            .thenReturn(List.of(AttributeInsightMother.aiInsightWithAttributeId(attribute.id())));

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
        verifyNoInteractions(createAttributeAiInsightHelper,
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
            .thenReturn(List.of(AttributeInsightMother.aiInsightWithAttributeId(attribute.id())));

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
        verifyNoInteractions(createAttributeAiInsightHelper,
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
