package org.flickit.assessment.core.application.service.insight;

import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.application.domain.kit.KitLanguage;
import org.flickit.assessment.common.application.port.out.ValidateAssessmentResultPort;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.CalculateNotValidException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.AssessmentResult;
import org.flickit.assessment.core.application.domain.SubjectValue;
import org.flickit.assessment.core.application.domain.insight.AssessmentInsight;
import org.flickit.assessment.core.application.domain.insight.AttributeInsight;
import org.flickit.assessment.core.application.domain.insight.SubjectInsight;
import org.flickit.assessment.core.application.port.in.insight.GenerateAllAssessmentInsightsUseCase.Param;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.core.application.port.out.attribute.LoadAttributesPort;
import org.flickit.assessment.core.application.port.out.insight.assessment.CreateAssessmentInsightPort;
import org.flickit.assessment.core.application.port.out.insight.assessment.LoadAssessmentInsightPort;
import org.flickit.assessment.core.application.port.out.insight.attribute.CreateAttributeInsightPort;
import org.flickit.assessment.core.application.port.out.insight.attribute.LoadAttributeInsightsPort;
import org.flickit.assessment.core.application.port.out.insight.subject.CreateSubjectInsightPort;
import org.flickit.assessment.core.application.port.out.insight.subject.LoadSubjectInsightsPort;
import org.flickit.assessment.core.application.port.out.subject.LoadSubjectsPort;
import org.flickit.assessment.core.application.service.insight.assessment.CreateAssessmentInsightHelper;
import org.flickit.assessment.core.application.service.insight.attribute.CreateAttributeAiInsightHelper;
import org.flickit.assessment.core.application.service.insight.attribute.CreateAttributeAiInsightHelper.AttributeInsightsParam;
import org.flickit.assessment.core.application.service.insight.subject.CreateSubjectInsightsHelper;
import org.flickit.assessment.core.application.service.insight.subject.CreateSubjectInsightsHelper.SubjectInsightsParam;
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
import static org.flickit.assessment.core.test.fixture.application.AttributeInsightMother.aiInsightWithTime;
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
    private CreateSubjectInsightsHelper createSubjectInsightsHelper;

    @Mock
    private CreateSubjectInsightPort createSubjectInsightPort;

    @Mock
    private LoadAssessmentInsightPort loadAssessmentInsightPort;

    @Mock
    private CreateAssessmentInsightHelper createAssessmentInsightHelper;

    @Mock
    private CreateAssessmentInsightPort createAssessmentInsightPort;

    @Captor
    private ArgumentCaptor<AttributeInsightsParam> attributeHelperParamArgumentCaptor;

    @Captor
    private ArgumentCaptor<List<AttributeInsight>> attributeInsightArgumentCaptor;

    @Captor
    private ArgumentCaptor<SubjectInsightsParam> subjectHelperParamArgumentCaptor;

    @Captor
    private ArgumentCaptor<List<SubjectInsight>> subjectInsightArgumentCaptor;

    private final Param param = createParam(Param.ParamBuilder::build);
    private final AssessmentResult assessmentResult = AssessmentResultMother.validResultWithLanguage(KitLanguage.EN, KitLanguage.FA);
    private final LoadAttributesPort.Result attribute = createAttribute();
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
            createSubjectInsightsHelper,
            createSubjectInsightPort,
            loadAssessmentInsightPort,
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
            loadAttributesPort,
            loadAttributeInsightsPort,
            createAttributeAiInsightHelper,
            createAttributeInsightPort,
            loadSubjectsPort,
            loadSubjectInsightsPort,
            createSubjectInsightsHelper,
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

        verifyNoInteractions(loadAttributesPort,
            loadAttributeInsightsPort,
            createAttributeAiInsightHelper,
            createAttributeInsightPort,
            loadSubjectsPort,
            loadSubjectInsightsPort,
            createSubjectInsightsHelper,
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
        when(loadAttributesPort.loadAll(param.getAssessmentId())).thenReturn(List.of(attribute));
        when(loadAttributeInsightsPort.loadInsights(assessmentResult.getId())).thenReturn(List.of());

        when(createAttributeAiInsightHelper.createAttributeAiInsights(attributeHelperParamArgumentCaptor.capture()))
            .thenReturn(List.of(newAttributeAiInsight));

        when(loadSubjectsPort.loadByKitVersionIdWithAttributes(assessmentResult.getKitVersionId()))
            .thenReturn(List.of());
        when(loadSubjectInsightsPort.loadSubjectInsights(assessmentResult.getId())).thenReturn(List.of());
        when(loadAssessmentInsightPort.loadByAssessmentResultId(assessmentResult.getId()))
            .thenReturn(Optional.of(AssessmentInsightMother.createSimpleAssessmentInsight()));

        service.generateAllAssessmentInsights(param);

        assertEquals(assessmentResult, attributeHelperParamArgumentCaptor.getValue().assessmentResult());
        assertEquals(attribute.id(), attributeHelperParamArgumentCaptor.getValue().attributeIds().getFirst());
        assertEquals(Locale.of(assessmentResult.getLanguage().getCode()),
            attributeHelperParamArgumentCaptor.getValue().locale());

        verify(createAttributeInsightPort, times(1)).persistAll(attributeInsightArgumentCaptor.capture());
        assertEquals(newAttributeAiInsight, attributeInsightArgumentCaptor.getValue().getFirst());

        verify(validateAssessmentResultPort).validate(param.getAssessmentId());
        verifyNoInteractions(createSubjectInsightsHelper,
            createSubjectInsightPort,
            createAssessmentInsightPort);
    }

    @Test
    void testGenerateAllAssessmentInsights_whenOneSubjectInsightDoesNotExist_thenCreateSubjectInsightAndPersist() {
        var subject = subjectValue.getSubject();
        var newSubjectInsight = SubjectInsightMother.defaultSubjectInsight();
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), GENERATE_ALL_ASSESSMENT_INSIGHTS))
            .thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId()))
            .thenReturn(Optional.of(assessmentResult));
        doNothing().when(validateAssessmentResultPort).validate(param.getAssessmentId());

        when(loadAttributesPort.loadAll(param.getAssessmentId())).thenReturn(List.of(attribute));
        when(loadAttributeInsightsPort.loadInsights(assessmentResult.getId()))
            .thenReturn(List.of(AttributeInsightMother.aiInsightWithAttributeId(attribute.id())));

        when(loadSubjectsPort.loadByKitVersionIdWithAttributes(assessmentResult.getKitVersionId()))
            .thenReturn(List.of(subject));
        when(loadSubjectInsightsPort.loadSubjectInsights(assessmentResult.getId())).thenReturn(List.of());
        when(createSubjectInsightsHelper.createSubjectInsights(subjectHelperParamArgumentCaptor.capture()))
            .thenReturn(List.of(newSubjectInsight));
        doNothing().when(createSubjectInsightPort).persistAll(anyList());

        when(loadAssessmentInsightPort.loadByAssessmentResultId(assessmentResult.getId()))
            .thenReturn(Optional.of(AssessmentInsightMother.createSimpleAssessmentInsight()));

        service.generateAllAssessmentInsights(param);

        assertEquals(assessmentResult, subjectHelperParamArgumentCaptor.getValue().assessmentResult());
        assertEquals(List.of(subject.getId()), subjectHelperParamArgumentCaptor.getValue().subjectIds());
        assertEquals(Locale.of(assessmentResult.getLanguage().getCode()),
            subjectHelperParamArgumentCaptor.getValue().locale());

        verify(createSubjectInsightPort, times(1)).persistAll(subjectInsightArgumentCaptor.capture());

        var subjectInsightArgument = subjectInsightArgumentCaptor.getValue().getFirst();
        assertEquals(newSubjectInsight, subjectInsightArgument);

        verify(validateAssessmentResultPort).validate(param.getAssessmentId());
        verifyNoInteractions(createAttributeAiInsightHelper,
            createAttributeInsightPort,
            createAssessmentInsightPort);
    }

    @Test
    void testGenerateAllAssessmentInsights_whenAssessmentInsightDoesNotExist_thenCreateAssessmentInsightAndPersist() {
        var subject = subjectValue.getSubject();
        var newAssessmentInsight = AssessmentInsightMother.createDefaultInsightWithAssessmentResultId(assessmentResult.getId());
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), GENERATE_ALL_ASSESSMENT_INSIGHTS))
            .thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId()))
            .thenReturn(Optional.of(assessmentResult));
        doNothing().when(validateAssessmentResultPort).validate(param.getAssessmentId());

        when(loadAttributesPort.loadAll(param.getAssessmentId())).thenReturn(List.of(attribute));
        when(loadAttributeInsightsPort.loadInsights(assessmentResult.getId()))
            .thenReturn(List.of(AttributeInsightMother.aiInsightWithAttributeId(attribute.id())));

        when(loadSubjectsPort.loadByKitVersionIdWithAttributes(assessmentResult.getKitVersionId()))
            .thenReturn(List.of(subject));
        when(loadSubjectInsightsPort.loadSubjectInsights(assessmentResult.getId()))
            .thenReturn(List.of(SubjectInsightMother.subjectInsightWithSubjectId(subject.getId())));

        var locale = Locale.of(assessmentResult.getLanguage().getCode());

        when(loadAssessmentInsightPort.loadByAssessmentResultId(assessmentResult.getId())).thenReturn(Optional.empty());
        when(createAssessmentInsightHelper.createAssessmentInsight(assessmentResult, locale))
            .thenReturn(newAssessmentInsight);
        when(createAssessmentInsightPort.persist(any(AssessmentInsight.class))).thenReturn(UUID.randomUUID());
        service.generateAllAssessmentInsights(param);

        var assessmentInsightArgumentCaptor = ArgumentCaptor.forClass(AssessmentInsight.class);
        verify(createAssessmentInsightPort, times(1))
            .persist(assessmentInsightArgumentCaptor.capture());

        assertNull(assessmentInsightArgumentCaptor.getValue().getId());
        assertEquals(newAssessmentInsight, assessmentInsightArgumentCaptor.getValue());

        verify(validateAssessmentResultPort).validate(param.getAssessmentId());
        verifyNoInteractions(createAttributeAiInsightHelper,
            createAttributeInsightPort,
            createSubjectInsightsHelper,
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
