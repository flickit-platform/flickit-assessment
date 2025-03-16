package org.flickit.assessment.core.application.service.assessment;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.application.port.out.ValidateAssessmentResultPort;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.insight.Insight;
import org.flickit.assessment.core.application.port.in.insight.assessment.GetAssessmentInsightsIssuesUseCase;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.core.application.port.out.attribute.CountAttributesPort;
import org.flickit.assessment.core.application.port.out.subject.CountSubjectsPort;
import org.flickit.assessment.core.application.service.insight.assessment.GetAssessmentInsightHelper;
import org.flickit.assessment.core.application.service.insight.attribute.GetAttributeInsightHelper;
import org.flickit.assessment.core.application.service.insight.subject.GetSubjectInsightHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.VIEW_ASSESSMENT_INSIGHTS;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.core.common.ErrorMessageKey.GET_ASSESSMENT_INSIGHTS_ISSUES_ASSESSMENT_RESULT_NOT_FOUND;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetAssessmentInsightsIssuesService implements GetAssessmentInsightsIssuesUseCase {

    private final AssessmentAccessChecker assessmentAccessChecker;
    private final LoadAssessmentResultPort loadAssessmentResultPort;
    private final ValidateAssessmentResultPort validateAssessmentResultPort;
    private final GetAssessmentInsightHelper getAssessmentInsightHelper;
    private final GetSubjectInsightHelper getSubjectInsightHelper;
    private final GetAttributeInsightHelper getAttributeInsightHelper;
    private final CountSubjectsPort countSubjectsPort;
    private final CountAttributesPort countAttributesPort;

    @Override
    public Result getInsightsIssues(Param param) {
        if (!assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ASSESSMENT_INSIGHTS))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        var assessmentResult = loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())
            .orElseThrow(() -> new ResourceNotFoundException(GET_ASSESSMENT_INSIGHTS_ISSUES_ASSESSMENT_RESULT_NOT_FOUND));
        validateAssessmentResultPort.validate(param.getAssessmentId());

        var assessmentInsight = getAssessmentInsightHelper.getAssessmentInsight(assessmentResult, param.getCurrentUserId());
        var subjectsInsights = getSubjectInsightHelper.getSubjectInsights(assessmentResult, param.getCurrentUserId())
            .values().stream()
            .toList();
        var attributesInsights = getAttributeInsightHelper.getAttributeInsights(assessmentResult, param.getCurrentUserId())
            .values().stream()
            .toList();

        var subjectsCount = countSubjectsPort.countSubjects(assessmentResult.getKitVersionId());
        var attributesCount = countAttributesPort.countAttributes(assessmentResult.getKitVersionId());

        var expectedInsightsCount = attributesCount + subjectsCount + 1;
        var totalGeneratedInsights = attributesInsights.size() +
            subjectsInsights.size() +
            (Insight.isNotGenerated().test(assessmentInsight) ? 0 : 1);

        int notGeneratedInsights = Math.max(expectedInsightsCount - totalGeneratedInsights, 0);
        int unapprovedInsights = countUnapprovedInsights(assessmentInsight, subjectsInsights, attributesInsights);
        int expiredInsights = countExpiredInsights(assessmentInsight, subjectsInsights, attributesInsights, assessmentResult.getLastCalculationTime());

        return new Result(notGeneratedInsights, unapprovedInsights, expiredInsights);
    }

    private int countUnapprovedInsights(Insight assessmentInsight,
                                        List<Insight> subjectsInsights,
                                        List<Insight> attributesInsights) {
        var assessmentInsightUnapproved = Insight.isUnapproved().test(assessmentInsight) ? 1 : 0;

        var unapprovedSubjectsInsightsCount = subjectsInsights.stream()
            .filter(Insight.isUnapproved())
            .toList().size();
        var unapprovedAttributesInsightsCount = attributesInsights.stream()
            .filter(Insight.isUnapproved())
            .toList().size();

        return assessmentInsightUnapproved + unapprovedSubjectsInsightsCount + unapprovedAttributesInsightsCount;
    }

    private int countExpiredInsights(Insight assessmentInsight,
                                     List<Insight> subjectsInsights,
                                     List<Insight> attributesInsights,
                                     LocalDateTime lastCalculationTime) {
        var assessmentInsightExpired = Insight.isExpired(lastCalculationTime).test(assessmentInsight) ? 1 : 0;

        var expiredSubjectsInsightsCount = subjectsInsights.stream()
            .filter(Insight.isExpired(lastCalculationTime))
            .toList().size();
        var expiredAttributeInsightsCount = attributesInsights.stream()
            .filter(Insight.isExpired(lastCalculationTime))
            .toList().size();

        return expiredAttributeInsightsCount + expiredSubjectsInsightsCount + assessmentInsightExpired;
    }
}
