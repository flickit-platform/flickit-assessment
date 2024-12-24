package org.flickit.assessment.core.application.service.assessment;

import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.AttributeInsight;
import org.flickit.assessment.core.application.domain.ConfidenceLevel;
import org.flickit.assessment.core.application.domain.SubjectInsight;
import org.flickit.assessment.core.application.port.out.adviceitem.CountAdviceItemsPort;
import org.flickit.assessment.core.application.port.out.answer.CountLowConfidenceAnswersPort;
import org.flickit.assessment.core.application.port.out.assessment.GetAssessmentProgressPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.core.application.port.out.attribute.CountAttributesPort;
import org.flickit.assessment.core.application.port.out.attributeinsight.LoadAttributeInsightsPort;
import org.flickit.assessment.core.application.port.out.evidence.LoadEvidencesDashboardPort;
import org.flickit.assessment.core.application.port.out.subject.CountSubjectsPort;
import org.flickit.assessment.core.application.port.out.subjectinsight.LoadSubjectInsightsPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

import org.flickit.assessment.core.application.port.in.assessment.GetAssessmentDashboardUseCase;

import java.time.LocalDateTime;
import java.util.List;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.VIEW_DASHBOARD;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.core.common.ErrorMessageKey.GET_ASSESSMENT_DASHBOARD_ASSESSMENT_RESULT_NOT_FOUND;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetAssessmentDashboardService implements GetAssessmentDashboardUseCase {

    private final AssessmentAccessChecker assessmentAccessChecker;
    private final LoadAssessmentResultPort loadAssessmentResultPort;
    private final LoadAttributeInsightsPort loadAttributeInsightsPort;
    private final CountAdviceItemsPort loadAdvicesDashboardPort;
    private final LoadEvidencesDashboardPort loadEvidencesDashboardPort;
    private final CountAttributesPort countAttributesPort;
    private final CountSubjectsPort countSubjectsPort;
    private final GetAssessmentProgressPort getAssessmentProgressPort;
    private final CountLowConfidenceAnswersPort countLowConfidenceAnswersPort;
    private final LoadSubjectInsightsPort loadSubjectInsightsPort;

    @Override
    public Result getAssessmentDashboard(Param param) {
        if (!assessmentAccessChecker.isAuthorized(param.getId(), param.getCurrentUserId(), VIEW_DASHBOARD))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        var assessmentResult = loadAssessmentResultPort.loadByAssessmentId(param.getId()).
            orElseThrow(() -> new ResourceNotFoundException(GET_ASSESSMENT_DASHBOARD_ASSESSMENT_RESULT_NOT_FOUND));

        var countLowConfidenceAnswers = countLowConfidenceAnswersPort.countWithConfidenceLessThan(assessmentResult.getId(), ConfidenceLevel.SOMEWHAT_UNSURE);
        var progress = getAssessmentProgressPort.getProgress(param.getId());
        var evidencesResult = loadEvidencesDashboardPort.loadEvidencesDashboard(param.getId());
        var attributeInsights = loadAttributeInsightsPort.loadInsights(assessmentResult.getId());
        var subjectsInsights = loadSubjectInsightsPort.loadSubjectInsights(assessmentResult.getId());
        var attributesCount = countAttributesPort.countAttributes(assessmentResult.getKitVersionId());
        var subjectsCount = countSubjectsPort.countSubjects(assessmentResult.getKitVersionId());
        var advicesResult = loadAdvicesDashboardPort.countAdviceItems(assessmentResult.getId());

        return new Result(buildQuestionsResult(countLowConfidenceAnswers, progress, evidencesResult),
            buildInsightsResult(attributeInsights, subjectsInsights, assessmentResult.getLastCalculationTime(), attributesCount, subjectsCount),
            buildAdvices(advicesResult)
        );
    }

    private Result.Questions buildQuestionsResult(int countLowConfidenceAnswers, GetAssessmentProgressPort.Result progress, LoadEvidencesDashboardPort.Result evidencesResult) {
        return new Result.Questions(progress.questionsCount(),
            progress.answersCount(),
            progress.questionsCount() - progress.answersCount(),
            countLowConfidenceAnswers,
            progress.questionsCount() -
                evidencesResult.evidences().stream().filter(e -> e.type() != null).map(LoadEvidencesDashboardPort.Result.Evidence::questionId).distinct().count(),
            evidencesResult.evidences().stream().filter(e -> e.type() == null && e.resolved() == null).count());
    }

    private Result.Insights buildInsightsResult(List<AttributeInsight> attributeInsights, List<SubjectInsight> subjectsInsights, LocalDateTime lastCalculationTime, int attributesCount, int subjectsCount) {
        int total = attributesCount + subjectsCount + 1;
        var expiredAttributeInsights = attributeInsights
            .stream()
            .map(e -> e.getAiInsightTime().isBefore(e.getAssessorInsightTime()) ?
                e.getAssessorInsightTime() : e.getAiInsightTime())
            .filter(e -> e.isBefore(lastCalculationTime))
            .count();

        var expiredSubjectsInsights = subjectsInsights
            .stream()
            .filter(e -> e.getInsightTime().isBefore(lastCalculationTime))
            .count();

        return new Result.Insights(total,
            total - (attributeInsights.size() + subjectsInsights.size()),
            null,
            expiredAttributeInsights + expiredSubjectsInsights
        );
    }


    private Result.Advices buildAdvices(CountAdviceItemsPort.Result dashboardAdvicesResult) {
        return new Result.Advices(dashboardAdvicesResult.total());
    }
}
