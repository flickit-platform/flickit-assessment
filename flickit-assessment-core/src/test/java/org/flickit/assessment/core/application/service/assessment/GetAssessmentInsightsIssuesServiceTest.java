package org.flickit.assessment.core.application.service.assessment;

import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.application.port.out.ValidateAssessmentResultPort;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.AssessmentResult;
import org.flickit.assessment.core.application.port.in.insight.assessment.GetAssessmentInsightsIssuesUseCase;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.core.application.port.out.attribute.CountAttributesPort;
import org.flickit.assessment.core.application.port.out.subject.CountSubjectsPort;
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
import static org.flickit.assessment.core.common.ErrorMessageKey.GET_ASSESSMENT_INSIGHTS_ISSUES_ASSESSMENT_RESULT_NOT_FOUND;
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
    private GetSubjectInsightHelper getSubjectInsightHelper;

    @Mock
    private GetAttributeInsightHelper getAttributeInsightHelper;

    @Mock
    private CountSubjectsPort countSubjectsPort;

    @Mock
    private CountAttributesPort countAttributesPort;

    private final AssessmentResult assessmentResult = AssessmentResultMother.validResult();

    private final int subjectCount = 5;
    private final int attributeCount = 7;

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
            countSubjectsPort,
            getSubjectInsightHelper,
            countAttributesPort,
            getAttributeInsightHelper);
    }

    @Test
    void testGetAssessmentInsightsIssues_whenAssessmentResultNotFound_thenThrowResourceNotFoundException() {
        var param = createParam(GetAssessmentInsightsIssuesUseCase.Param.ParamBuilder::build);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ASSESSMENT_INSIGHTS))
            .thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.empty());

        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.getInsightsIssues(param));
        assertEquals(GET_ASSESSMENT_INSIGHTS_ISSUES_ASSESSMENT_RESULT_NOT_FOUND, throwable.getMessage());

        verifyNoInteractions(validateAssessmentResultPort,
            getAssessmentInsightHelper,
            countSubjectsPort,
            getSubjectInsightHelper,
            countAttributesPort,
            getAttributeInsightHelper);
    }

    @Test
    void testGetAssessmentInsightsIssues_whenNoInsightExists_thenReturnsEmptyInsights() {
        var param = createParam(GetAssessmentInsightsIssuesUseCase.Param.ParamBuilder::build);
        var emptyInsight = InsightMother.emptyInsight();

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ASSESSMENT_INSIGHTS))
            .thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId()))
            .thenReturn(Optional.of(assessmentResult));
        doNothing().when(validateAssessmentResultPort).validate(param.getAssessmentId());
        when(getAssessmentInsightHelper.getAssessmentInsight(assessmentResult, param.getCurrentUserId()))
            .thenReturn(emptyInsight);
        when(countSubjectsPort.countSubjects(assessmentResult.getKitVersionId()))
            .thenReturn(subjectCount);
        when(getSubjectInsightHelper.getSubjectInsights(assessmentResult, param.getCurrentUserId()))
            .thenReturn(Map.of());
        when(countAttributesPort.countAttributes(assessmentResult.getKitVersionId()))
            .thenReturn(attributeCount);
        when(getAttributeInsightHelper.getAttributeInsights(assessmentResult, param.getCurrentUserId()))
            .thenReturn(Map.of());

        var result = service.getInsightsIssues(param);

        assertEquals(13, result.notGenerated());
        assertEquals(0, result.unapproved());
        assertEquals(0, result.expired());
    }

    @Test
    void testGetAssessmentInsightsIssues_whenAssessmentInsightIsExpired_thenReturnsExpiredInsight() {
        var param = createParam(GetAssessmentInsightsIssuesUseCase.Param.ParamBuilder::build);
        var expiredInsight = InsightMother.defaultInsightWithMinLastModificationTime();

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ASSESSMENT_INSIGHTS))
            .thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId()))
            .thenReturn(Optional.of(assessmentResult));
        doNothing().when(validateAssessmentResultPort).validate(param.getAssessmentId());
        when(getAssessmentInsightHelper.getAssessmentInsight(assessmentResult, param.getCurrentUserId()))
            .thenReturn(expiredInsight);
        when(countSubjectsPort.countSubjects(assessmentResult.getKitVersionId()))
            .thenReturn(subjectCount);
        when(getSubjectInsightHelper.getSubjectInsights(assessmentResult, param.getCurrentUserId()))
            .thenReturn(Map.of());
        when(countAttributesPort.countAttributes(assessmentResult.getKitVersionId()))
            .thenReturn(attributeCount);
        when(getAttributeInsightHelper.getAttributeInsights(assessmentResult, param.getCurrentUserId()))
            .thenReturn(Map.of());

        var result = service.getInsightsIssues(param);

        assertEquals(12, result.notGenerated());
        assertEquals(0, result.unapproved());
        assertEquals(1, result.expired());
    }

    @Test
    void testGetAssessmentInsightsIssues_whenAssessmentInsightIsExpiredAndSubjectInsightIsExpiredAndUnapproved_thenReturnsResult() {
        var param = createParam(GetAssessmentInsightsIssuesUseCase.Param.ParamBuilder::build);
        var assessmentInsight = InsightMother.defaultInsightWithMinLastModificationTime();
        var subjectInsight = InsightMother.unapprovedAssessorInsightWithMinLastModificationTime();
        var subjectValue1 = SubjectValueMother.createSubjectValue();

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ASSESSMENT_INSIGHTS))
            .thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId()))
            .thenReturn(Optional.of(assessmentResult));
        doNothing().when(validateAssessmentResultPort).validate(param.getAssessmentId());
        when(getAssessmentInsightHelper.getAssessmentInsight(assessmentResult, param.getCurrentUserId()))
            .thenReturn(assessmentInsight);
        when(countSubjectsPort.countSubjects(assessmentResult.getKitVersionId()))
            .thenReturn(subjectCount);
        when(getSubjectInsightHelper.getSubjectInsights(assessmentResult, param.getCurrentUserId()))
            .thenReturn(Map.of(subjectValue1.getSubject().getId(), subjectInsight));
        when(countAttributesPort.countAttributes(assessmentResult.getKitVersionId()))
            .thenReturn(attributeCount);
        when(getAttributeInsightHelper.getAttributeInsights(assessmentResult, param.getCurrentUserId()))
            .thenReturn(Map.of());

        var result = service.getInsightsIssues(param);

        assertEquals(11, result.notGenerated());
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
        when(countSubjectsPort.countSubjects(assessmentResult.getKitVersionId()))
            .thenReturn(List.of(subjectValue1).size());
        when(getSubjectInsightHelper.getSubjectInsights(assessmentResult, param.getCurrentUserId()))
            .thenReturn(Map.of(subjectValue1.getSubject().getId(), defaultInsight));
        when(countAttributesPort.countAttributes(assessmentResult.getKitVersionId()))
            .thenReturn(List.of(attributeValues1).size());
        when(getAttributeInsightHelper.getAttributeInsights(assessmentResult, param.getCurrentUserId()))
            .thenReturn(Map.of(
                attributeValues1.getFirst().getAttribute().getId(), defaultInsight,
                attributeValues1.get(1).getAttribute().getId(), defaultInsight));

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
