package org.flickit.assessment.core.application.service.insight;

import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.application.port.out.ValidateAssessmentResultPort;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.CalculateNotValidException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.AssessmentResult;
import org.flickit.assessment.core.application.domain.MaturityLevel;
import org.flickit.assessment.core.application.domain.insight.AssessmentInsight;
import org.flickit.assessment.core.application.domain.insight.SubjectInsight;
import org.flickit.assessment.core.application.port.in.insight.RegenerateAllAssessmentInsightsUseCase.Param;
import org.flickit.assessment.core.application.port.out.assessment.GetAssessmentProgressPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.core.application.port.out.attribute.LoadAttributesPort;
import org.flickit.assessment.core.application.port.out.insight.assessment.LoadAssessmentInsightPort;
import org.flickit.assessment.core.application.port.out.insight.assessment.UpdateAssessmentInsightPort;
import org.flickit.assessment.core.application.port.out.insight.attribute.LoadAttributeInsightsPort;
import org.flickit.assessment.core.application.port.out.insight.attribute.UpdateAttributeInsightPort;
import org.flickit.assessment.core.application.port.out.insight.subject.CreateSubjectInsightPort;
import org.flickit.assessment.core.application.port.out.insight.subject.LoadSubjectInsightsPort;
import org.flickit.assessment.core.application.port.out.maturitylevel.LoadMaturityLevelsPort;
import org.flickit.assessment.core.application.service.insight.assessment.CreateAssessmentInsightHelper;
import org.flickit.assessment.core.application.service.insight.attribute.CreateAttributeAiInsightHelper;
import org.flickit.assessment.core.application.service.insight.subject.CreateSubjectInsightsHelper;
import org.flickit.assessment.core.application.service.insight.subject.CreateSubjectInsightsHelper.SubjectInsightsParam;
import org.flickit.assessment.core.test.fixture.application.AssessmentInsightMother;
import org.flickit.assessment.core.test.fixture.application.AssessmentResultMother;
import org.flickit.assessment.core.test.fixture.application.AttributeInsightMother;
import org.flickit.assessment.core.test.fixture.application.MaturityLevelMother;
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
import static org.flickit.assessment.core.test.fixture.application.AssessmentInsightMother.createDefaultInsightWithAssessmentResultId;
import static org.flickit.assessment.core.test.fixture.application.AssessmentInsightMother.createDefaultInsightWithTimesAndApprove;
import static org.flickit.assessment.core.test.fixture.application.AttributeInsightMother.aiInsightWithAttributeId;
import static org.flickit.assessment.core.test.fixture.application.AttributeInsightMother.aiInsightWithTime;
import static org.flickit.assessment.core.test.fixture.application.SubjectInsightMother.defaultSubjectInsight;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegenerateAllAssessmentInsightsServiceTest {

    @InjectMocks
    private RegenerateAllAssessmentInsightsService service;

    @Mock
    private AssessmentAccessChecker assessmentAccessChecker;

    @Mock
    private ValidateAssessmentResultPort validateAssessmentResultPort;

    @Mock
    private GetAssessmentProgressPort getAssessmentProgressPort;

    @Mock
    private LoadAssessmentResultPort loadAssessmentResultPort;

    @Mock
    private LoadAttributeInsightsPort loadAttributeInsightsPort;

    @Mock
    private LoadMaturityLevelsPort loadMaturityLevelsPort;

    @Mock
    private CreateAttributeAiInsightHelper createAttributeAiInsightHelper;

    @Mock
    private UpdateAttributeInsightPort updateAttributeInsightPort;

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
    private UpdateAssessmentInsightPort updateAssessmentInsightPort;

    @Captor
    private ArgumentCaptor<CreateAttributeAiInsightHelper.Param> attributeHelperParamArgumentCaptor;

    @Captor
    private ArgumentCaptor<List<UpdateAttributeInsightPort.AiParam>> attributeInsightArgumentCaptor;

    @Captor
    private ArgumentCaptor<SubjectInsightsParam> subjectHelperParamArgumentCaptor;

    @Captor
    private ArgumentCaptor<List<SubjectInsight>> subjectInsightArgumentCaptor;

    private final Param param = createParam(Param.ParamBuilder::build);
    private final AssessmentResult assessmentResult = AssessmentResultMother.validResult();
    private final LoadAttributesPort.Result attribute = createAttribute();
    private final List<MaturityLevel> maturityLevels = MaturityLevelMother.allLevels();
    private final GetAssessmentProgressPort.Result progress = new GetAssessmentProgressPort.Result(param.getAssessmentId(), 10, 10);

    @Test
    void testRegenerateAllAssessmentInsights_whenCurrentUserDoesNotHaveRequiredPermission_thenThrowAccessDeniedException() {
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), GENERATE_ALL_ASSESSMENT_INSIGHTS)).thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.regenerateAllAssessmentInsights(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verifyNoInteractions(validateAssessmentResultPort,
            loadAssessmentResultPort,
            loadAttributeInsightsPort,
            loadMaturityLevelsPort,
            createAttributeAiInsightHelper,
            updateAttributeInsightPort,
            loadSubjectInsightsPort,
            createSubjectInsightsHelper,
            createSubjectInsightPort,
            loadAssessmentInsightPort,
            getAssessmentProgressPort,
            updateAssessmentInsightPort);
    }

    @Test
    void testRegenerateAllAssessmentInsights_whenAssessmentResultIsNotFound_thenThrowResourceNotFoundException() {
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), GENERATE_ALL_ASSESSMENT_INSIGHTS))
            .thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId()))
            .thenReturn(Optional.empty());

        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.regenerateAllAssessmentInsights(param));
        assertEquals(COMMON_ASSESSMENT_RESULT_NOT_FOUND, throwable.getMessage());

        verifyNoInteractions(validateAssessmentResultPort,
            getAssessmentProgressPort,
            loadAttributeInsightsPort,
            createAttributeAiInsightHelper,
            loadMaturityLevelsPort,
            updateAttributeInsightPort,
            loadSubjectInsightsPort,
            createSubjectInsightsHelper,
            createSubjectInsightPort,
            loadAssessmentInsightPort,
            updateAssessmentInsightPort);
    }

    @Test
    void testRegenerateAllAssessmentInsights_whenCalculatedResultIsNotValid_thenThrowCalculateNotValidException() {
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), GENERATE_ALL_ASSESSMENT_INSIGHTS))
            .thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId()))
            .thenReturn(Optional.of(assessmentResult));
        doThrow(new CalculateNotValidException(COMMON_ASSESSMENT_RESULT_NOT_VALID))
            .when(validateAssessmentResultPort).validate(param.getAssessmentId());

        var throwable = assertThrows(CalculateNotValidException.class, () -> service.regenerateAllAssessmentInsights(param));
        assertEquals(COMMON_ASSESSMENT_RESULT_NOT_VALID, throwable.getMessage());

        verifyNoInteractions(getAssessmentProgressPort,
            loadAttributeInsightsPort,
            loadMaturityLevelsPort,
            createAttributeAiInsightHelper,
            updateAttributeInsightPort,
            loadSubjectInsightsPort,
            createSubjectInsightsHelper,
            createSubjectInsightPort,
            loadAssessmentInsightPort,
            updateAssessmentInsightPort);
    }

    @Test
    void testRegenerateAllAssessmentInsights_whenOneAttributeInsightExpired_thenCreateAttributeAiInsightAndPersist() {
        var expiredAttributeInsight = aiInsightWithTime(LocalDateTime.now().minusDays(1));
        var newAttributeAiInsight = aiInsightWithAttributeId(expiredAttributeInsight.getAttributeId());

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), GENERATE_ALL_ASSESSMENT_INSIGHTS))
            .thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId()))
            .thenReturn(Optional.of(assessmentResult));
        doNothing().when(validateAssessmentResultPort).validate(param.getAssessmentId());
        when(loadAttributeInsightsPort.loadInsights(assessmentResult.getId())).thenReturn(List.of(expiredAttributeInsight));

        when(loadMaturityLevelsPort.loadByKitVersionId(assessmentResult.getKitVersionId()))
            .thenReturn(maturityLevels);
        when(getAssessmentProgressPort.getProgress(assessmentResult.getAssessment().getId())).thenReturn(progress);
        when(createAttributeAiInsightHelper.createAttributeAiInsight(attributeHelperParamArgumentCaptor.capture()))
            .thenReturn(newAttributeAiInsight);

        when(loadSubjectInsightsPort.loadSubjectInsights(assessmentResult.getId())).thenReturn(List.of());
        when(loadAssessmentInsightPort.loadByAssessmentResultId(assessmentResult.getId()))
            .thenReturn(Optional.of(AssessmentInsightMother.createWithAssessmentResultId(assessmentResult.getId())));

        service.regenerateAllAssessmentInsights(param);

        assertEquals(assessmentResult, attributeHelperParamArgumentCaptor.getValue().assessmentResult());
        assertEquals(expiredAttributeInsight.getAttributeId(), attributeHelperParamArgumentCaptor.getValue().attributeId());
        assertEquals(maturityLevels, attributeHelperParamArgumentCaptor.getValue().maturityLevels());
        assertEquals(progress, attributeHelperParamArgumentCaptor.getValue().assessmentProgress());
        assertEquals(Locale.of(assessmentResult.getAssessment().getAssessmentKit().getLanguage().getCode()),
            attributeHelperParamArgumentCaptor.getValue().locale());

        verify(updateAttributeInsightPort, times(1)).updateAiInsights(attributeInsightArgumentCaptor.capture());
        var aiParam = attributeInsightArgumentCaptor.getValue().getFirst();
        assertEquals(newAttributeAiInsight.getAssessmentResultId(), aiParam.assessmentResultId());
        assertEquals(newAttributeAiInsight.getAttributeId(), aiParam.attributeId());
        assertEquals(newAttributeAiInsight.getAiInsight(), aiParam.aiInsight());
        assertEquals(newAttributeAiInsight.getAiInsightTime(), aiParam.aiInsightTime());
        assertEquals(newAttributeAiInsight.getAiInputPath(), aiParam.aiInputPath());
        assertEquals(newAttributeAiInsight.isApproved(), aiParam.isApproved());
        assertEquals(newAttributeAiInsight.getLastModificationTime(), aiParam.lastModificationTime());

        verify(validateAssessmentResultPort).validate(param.getAssessmentId());
        verifyNoInteractions(createSubjectInsightsHelper,
            createSubjectInsightPort,
            updateAssessmentInsightPort);
    }

    @Test
    void testRegenerateAllAssessmentInsights_whenOneSubjectInsightExpired_thenCreateSubjectInsightAndPersist() {
        var oldInsightTime = LocalDateTime.now().minusDays(1);
        var expiredSubjectInsight = defaultSubjectInsight(oldInsightTime, oldInsightTime, false);
        var newSubjectInsight = defaultSubjectInsight();
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), GENERATE_ALL_ASSESSMENT_INSIGHTS))
            .thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId()))
            .thenReturn(Optional.of(assessmentResult));
        doNothing().when(validateAssessmentResultPort).validate(param.getAssessmentId());

        when(loadAttributeInsightsPort.loadInsights(assessmentResult.getId()))
            .thenReturn(List.of(AttributeInsightMother.aiInsightWithAttributeId(attribute.id())));

        when(loadSubjectInsightsPort.loadSubjectInsights(assessmentResult.getId()))
            .thenReturn(List.of(expiredSubjectInsight));
        when(createSubjectInsightsHelper.createSubjectInsights(subjectHelperParamArgumentCaptor.capture()))
            .thenReturn(List.of(newSubjectInsight));
        doNothing().when(createSubjectInsightPort).persistAll(anyList());

        when(loadAssessmentInsightPort.loadByAssessmentResultId(assessmentResult.getId()))
            .thenReturn(Optional.of(AssessmentInsightMother.createSimpleAssessmentInsight()));

        service.regenerateAllAssessmentInsights(param);

        assertEquals(assessmentResult, subjectHelperParamArgumentCaptor.getValue().assessmentResult());
        assertEquals(List.of(expiredSubjectInsight.getSubjectId()), subjectHelperParamArgumentCaptor.getValue().subjectIds());
        assertEquals(Locale.of(assessmentResult.getAssessment().getAssessmentKit().getLanguage().getCode()),
            subjectHelperParamArgumentCaptor.getValue().locale());

        verify(createSubjectInsightPort, times(1)).persistAll(subjectInsightArgumentCaptor.capture());

        var subjectInsightArgument = subjectInsightArgumentCaptor.getValue().getFirst();
        assertEquals(newSubjectInsight, subjectInsightArgument);

        verify(validateAssessmentResultPort).validate(param.getAssessmentId());
        verifyNoInteractions(loadMaturityLevelsPort,
            getAssessmentProgressPort,
            createAttributeAiInsightHelper,
            updateAttributeInsightPort,
            updateAssessmentInsightPort);
    }

    @Test
    void testRegenerateAllAssessmentInsights_whenAssessmentInsightExpired_thenCreateAssessmentInsightAndPersist() {
        var oldInsightTime = LocalDateTime.now().minusDays(1);
        var expiredAssessmentInsight = createDefaultInsightWithTimesAndApprove(oldInsightTime, oldInsightTime, false);
        var newAssessmentInsight = createDefaultInsightWithAssessmentResultId(assessmentResult.getId());
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), GENERATE_ALL_ASSESSMENT_INSIGHTS))
            .thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId()))
            .thenReturn(Optional.of(assessmentResult));
        doNothing().when(validateAssessmentResultPort).validate(param.getAssessmentId());

        when(loadAttributeInsightsPort.loadInsights(assessmentResult.getId()))
            .thenReturn(List.of(AttributeInsightMother.aiInsightWithAttributeId(attribute.id())));

        when(loadSubjectInsightsPort.loadSubjectInsights(assessmentResult.getId())).thenReturn(List.of());

        var locale = Locale.of(assessmentResult.getAssessment().getAssessmentKit().getLanguage().getCode());

        when(loadAssessmentInsightPort.loadByAssessmentResultId(assessmentResult.getId()))
            .thenReturn(Optional.of(expiredAssessmentInsight));
        when(createAssessmentInsightHelper.createAssessmentInsight(assessmentResult, locale))
            .thenReturn(newAssessmentInsight);
        doNothing()
            .when(updateAssessmentInsightPort).updateInsight(any(AssessmentInsight.class));
        service.regenerateAllAssessmentInsights(param);

        var assessmentInsightArgumentCaptor = ArgumentCaptor.forClass(AssessmentInsight.class);
        verify(updateAssessmentInsightPort, times(1))
            .updateInsight(assessmentInsightArgumentCaptor.capture());

        assertEquals(expiredAssessmentInsight.getId(), assessmentInsightArgumentCaptor.getValue().getId());
        assertEquals(newAssessmentInsight.getAssessmentResultId(),
            assessmentInsightArgumentCaptor.getValue().getAssessmentResultId());
        assertEquals(newAssessmentInsight.getInsightBy(), assessmentInsightArgumentCaptor.getValue().getInsightBy());
        assertEquals(newAssessmentInsight.getInsightTime(), assessmentInsightArgumentCaptor.getValue().getInsightTime());
        assertEquals(newAssessmentInsight.getLastModificationTime(),
            assessmentInsightArgumentCaptor.getValue().getLastModificationTime());
        assertEquals(newAssessmentInsight.getInsight(), assessmentInsightArgumentCaptor.getValue().getInsight());
        assertEquals(newAssessmentInsight.isApproved(), assessmentInsightArgumentCaptor.getValue().isApproved());

        verify(validateAssessmentResultPort).validate(param.getAssessmentId());
        verifyNoInteractions(loadMaturityLevelsPort,
            getAssessmentProgressPort,
            createAttributeAiInsightHelper,
            updateAttributeInsightPort,
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
