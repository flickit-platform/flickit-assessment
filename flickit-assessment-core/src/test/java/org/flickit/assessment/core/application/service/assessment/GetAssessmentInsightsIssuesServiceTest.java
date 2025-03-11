package org.flickit.assessment.core.application.service.assessment;

import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.application.port.out.ValidateAssessmentResultPort;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.AssessmentResult;
import org.flickit.assessment.core.application.port.in.insight.assessment.GetAssessmentInsightsIssuesUseCase;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.core.application.port.out.attributevalue.LoadAttributeValuePort;
import org.flickit.assessment.core.application.port.out.subjectvalue.LoadSubjectValuePort;
import org.flickit.assessment.core.application.service.insight.assessment.GetAssessmentInsightHelper;
import org.flickit.assessment.core.application.service.insight.attribute.GetAttributeInsightHelper;
import org.flickit.assessment.core.application.service.insight.subject.GetSubjectInsightHelper;
import org.flickit.assessment.core.test.fixture.application.AssessmentResultMother;
import org.flickit.assessment.core.test.fixture.application.InsightMother;
import org.flickit.assessment.core.test.fixture.application.SubjectValueMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.*;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.core.common.ErrorMessageKey.GET_ASSESSMENT_INSIGHT_ASSESSMENT_RESULT_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.doNothing;

@ExtendWith(MockitoExtension.class)
class GetAssessmentInsightsIssuesServiceTest {

    @InjectMocks
    private GetAssessmentInsightsIssuesService service;

    @Mock
    private AssessmentAccessChecker assessmentAccessChecker;

    @Mock
    private LoadAssessmentResultPort loadAssessmentResultPort;

    @Mock
    private ValidateAssessmentResultPort validateAssessmentResultPort;

    @Mock
    private GetAssessmentInsightHelper getAssessmentInsightHelper;

    @Mock
    private LoadSubjectValuePort loadSubjectValuePort;

    @Mock
    private GetSubjectInsightHelper getSubjectInsightHelper;

    @Mock
    private LoadAttributeValuePort loadAttributeValuePort;

    @Mock
    private GetAttributeInsightHelper getAttributeInsightHelper;

    private final AssessmentResult assessmentResult = AssessmentResultMother.validResult();

    @Test
    void testGetAssessmentInsightsIssues_whenCurrentUserDoesNotHaveRequiredPermissions_thenThrowAccessDeniedException() {
        var param = createParam(GetAssessmentInsightsIssuesUseCase.Param.ParamBuilder::build);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ASSESSMENT_INSIGHTS))
            .thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.getInsightsIssues(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verifyNoInteractions(loadAssessmentResultPort,
            validateAssessmentResultPort,
            getAssessmentInsightHelper,
            loadSubjectValuePort,
            getSubjectInsightHelper,
            loadAttributeValuePort,
            getAttributeInsightHelper);
    }

    @Test
    void testGetAssessmentInsightsIssues_whenAssessmentResultNotFound_thenThrowResourceNotFoundException() {
        var param = createParam(GetAssessmentInsightsIssuesUseCase.Param.ParamBuilder::build);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ASSESSMENT_INSIGHTS))
            .thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.empty());

        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.getInsightsIssues(param));
        assertEquals(GET_ASSESSMENT_INSIGHT_ASSESSMENT_RESULT_NOT_FOUND, throwable.getMessage());

        verifyNoInteractions(validateAssessmentResultPort,
            getAssessmentInsightHelper,
            loadSubjectValuePort,
            getSubjectInsightHelper,
            loadAttributeValuePort,
            getAttributeInsightHelper);
    }

    @Test
    void testGetAssessmentInsightsIssues_whenNoInsightExists_thenReturnsEmptyInsights() {
        var param = createParam(GetAssessmentInsightsIssuesUseCase.Param.ParamBuilder::build);
        var emptyInsight = InsightMother.emptyInsight();
        var subjectValue1 = SubjectValueMother.createSubjectValue();
        var attributeValues1 = subjectValue1.getAttributeValues();

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ASSESSMENT_INSIGHTS))
            .thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId()))
            .thenReturn(Optional.of(assessmentResult));
        doNothing().when(validateAssessmentResultPort).validate(param.getAssessmentId());
        when(getAssessmentInsightHelper.getAssessmentInsight(assessmentResult, param.getCurrentUserId()))
            .thenReturn(emptyInsight);
        when(loadSubjectValuePort.loadAll(assessmentResult.getId()))
            .thenReturn(List.of(subjectValue1));
        when(getSubjectInsightHelper.getSubjectInsights(assessmentResult, param.getCurrentUserId()))
            .thenReturn(Map.of());
        when(loadAttributeValuePort.loadAll(assessmentResult.getId()))
            .thenReturn(attributeValues1);
        when(getAttributeInsightHelper.getAttributeInsights(assessmentResult, param.getCurrentUserId()))
            .thenReturn(Map.of());
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_SUBJECT_INSIGHT))
            .thenReturn(true);
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_ATTRIBUTE_INSIGHT))
            .thenReturn(true);

        var result = service.getInsightsIssues(param);

        assertEquals(4, result.notGenerated());
        assertEquals(0, result.unapproved());
        assertEquals(0, result.expired());
    }

    @Test
    void testGetAssessmentInsightsIssues_whenAssessmentInsightIsExpired_thenReturnsExpiredInsight() {
        var param = createParam(GetAssessmentInsightsIssuesUseCase.Param.ParamBuilder::build);
        var expiredInsight = InsightMother.defaultInsightWithMinLastModificationTime();
        var subjectValue1 = SubjectValueMother.createSubjectValue();
        var attributeValues1 = subjectValue1.getAttributeValues();

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ASSESSMENT_INSIGHTS))
            .thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId()))
            .thenReturn(Optional.of(assessmentResult));
        doNothing().when(validateAssessmentResultPort).validate(param.getAssessmentId());
        when(getAssessmentInsightHelper.getAssessmentInsight(assessmentResult, param.getCurrentUserId()))
            .thenReturn(expiredInsight);
        when(loadSubjectValuePort.loadAll(assessmentResult.getId()))
            .thenReturn(List.of(subjectValue1));
        when(getSubjectInsightHelper.getSubjectInsights(assessmentResult, param.getCurrentUserId()))
            .thenReturn(Map.of());
        when(loadAttributeValuePort.loadAll(assessmentResult.getId()))
            .thenReturn(attributeValues1);
        when(getAttributeInsightHelper.getAttributeInsights(assessmentResult, param.getCurrentUserId()))
            .thenReturn(Map.of());
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_SUBJECT_INSIGHT))
            .thenReturn(true);
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_ATTRIBUTE_INSIGHT))
            .thenReturn(true);

        var result = service.getInsightsIssues(param);

        assertEquals(3, result.notGenerated());
        assertEquals(0, result.unapproved());
        assertEquals(1, result.expired());
    }

    @Test
    void testGetAssessmentInsightsIssues_whenAssessmentInsightIsExpiredAndSubjectInsightIsExpiredAndUnapproved_thenReturnsResult() {
        var param = createParam(GetAssessmentInsightsIssuesUseCase.Param.ParamBuilder::build);
        var assessmentInsight = InsightMother.defaultInsightWithMinLastModificationTime();
        var subjectInsight = InsightMother.unapprovedAssessorInsightWithMinLastModificationTime();
        var subjectValue1 = SubjectValueMother.createSubjectValue();
        var attributeValues1 = subjectValue1.getAttributeValues();

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ASSESSMENT_INSIGHTS))
            .thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId()))
            .thenReturn(Optional.of(assessmentResult));
        doNothing().when(validateAssessmentResultPort).validate(param.getAssessmentId());
        when(getAssessmentInsightHelper.getAssessmentInsight(assessmentResult, param.getCurrentUserId()))
            .thenReturn(assessmentInsight);
        when(loadSubjectValuePort.loadAll(assessmentResult.getId()))
            .thenReturn(List.of(subjectValue1));
        when(getSubjectInsightHelper.getSubjectInsights(assessmentResult, param.getCurrentUserId()))
            .thenReturn(Map.of(subjectValue1.getSubject().getId(), subjectInsight));
        when(loadAttributeValuePort.loadAll(assessmentResult.getId()))
            .thenReturn(attributeValues1);
        when(getAttributeInsightHelper.getAttributeInsights(assessmentResult, param.getCurrentUserId()))
            .thenReturn(Map.of());
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_SUBJECT_INSIGHT))
            .thenReturn(true);
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_ATTRIBUTE_INSIGHT))
            .thenReturn(true);

        var result = service.getInsightsIssues(param);

        assertEquals(2, result.notGenerated());
        assertEquals(1, result.unapproved());
        assertEquals(2, result.expired());
    }

    @Test
    void GetAssessmentInsightsIssues_whenAllInsightsExistsAndAssessmentInsightIsExpiredAndUnapproved_thenReturnsAllInsights() {
        var param = createParam(GetAssessmentInsightsIssuesUseCase.Param.ParamBuilder::build);
        var assessmentInsight = InsightMother.unapprovedAssessorInsightWithMinLastModificationTime();
        var defaultInsight = InsightMother.defaultInsight();
        var subjectValue1 = SubjectValueMother.createSubjectValue();
        var attributeValues1 = subjectValue1.getAttributeValues();

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ASSESSMENT_INSIGHTS))
            .thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId()))
            .thenReturn(Optional.of(assessmentResult));
        doNothing().when(validateAssessmentResultPort).validate(param.getAssessmentId());
        when(getAssessmentInsightHelper.getAssessmentInsight(assessmentResult, param.getCurrentUserId()))
            .thenReturn(assessmentInsight);
        when(loadSubjectValuePort.loadAll(assessmentResult.getId()))
            .thenReturn(List.of(subjectValue1));
        when(getSubjectInsightHelper.getSubjectInsights(assessmentResult, param.getCurrentUserId()))
            .thenReturn(Map.of(subjectValue1.getSubject().getId(), defaultInsight));
        when(loadAttributeValuePort.loadAll(assessmentResult.getId()))
            .thenReturn(attributeValues1);
        when(getAttributeInsightHelper.getAttributeInsights(assessmentResult, param.getCurrentUserId()))
            .thenReturn(Map.of(
                attributeValues1.getFirst().getAttribute().getId(), defaultInsight,
                attributeValues1.get(1).getAttribute().getId(), defaultInsight));
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_SUBJECT_INSIGHT))
            .thenReturn(true);
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_ATTRIBUTE_INSIGHT))
            .thenReturn(true);

        var result = service.getInsightsIssues(param);

        assertEquals(0, result.notGenerated());
        assertEquals(1, result.unapproved());
        assertEquals(1, result.expired());
    }

    private GetAssessmentInsightsIssuesUseCase.Param createParam(Consumer<GetAssessmentInsightsIssuesUseCase.Param.ParamBuilder> changer) {
        var param = paramBuilder();
        changer.accept(param);
        return param.build();
    }

    private GetAssessmentInsightsIssuesUseCase.Param.ParamBuilder paramBuilder() {
        return GetAssessmentInsightsIssuesUseCase.Param.builder()
            .assessmentId(UUID.randomUUID())
            .currentUserId(UUID.randomUUID());
    }

}
