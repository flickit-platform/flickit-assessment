package org.flickit.assessment.core.application.service.assessment;

import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.port.out.adviceitem.CountAdviceItemsPort;
import org.flickit.assessment.core.application.port.out.assessment.GetAssessmentProgressPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.core.application.port.out.answer.LoadQuestionsAnswerDashboardPort;
import org.flickit.assessment.core.application.port.out.attribute.CountAttributesPort;
import org.flickit.assessment.core.application.port.out.attributeinsight.LoadInsightsDashboardPort;
import org.flickit.assessment.core.application.port.out.evidence.LoadEvidencesDashboardPort;
import org.flickit.assessment.core.application.port.out.subject.CountSubjectsPort;
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
    private final LoadQuestionsAnswerDashboardPort loadQuestionsAnswerDashboardPort;
    private final LoadInsightsDashboardPort loadInsightsDashboardPort;
    private final CountAdviceItemsPort loadAdvicesDashboardPort;
    private final LoadEvidencesDashboardPort loadEvidencesDashboardPort;
    private final CountAttributesPort countAttributesPort;
    private final CountSubjectsPort countSubjectsPort;
    private final GetAssessmentProgressPort getAssessmentProgressPort;

    @Override
    public Result getAssessmentDashboard(Param param) {
        if (!assessmentAccessChecker.isAuthorized(param.getId(), param.getCurrentUserId(), VIEW_DASHBOARD))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        var assessmentResult = loadAssessmentResultPort.loadByAssessmentId(param.getId()).
            orElseThrow(() -> new ResourceNotFoundException(GET_ASSESSMENT_DASHBOARD_ASSESSMENT_RESULT_NOT_FOUND));

        var questionsPortResult = loadQuestionsAnswerDashboardPort.loadQuestionsDashboard(assessmentResult.getId(), assessmentResult.getKitVersionId());
        var progress = getAssessmentProgressPort.getProgress(param.getId());
        var evidencesResult = loadEvidencesDashboardPort.loadEvidencesDashboard(param.getId());
        var insightsResult = loadInsightsDashboardPort.loadInsights(assessmentResult.getId());
        var attributesCount = countAttributesPort.countAttributes(assessmentResult.getKitVersionId());
        var subjectsCount = countSubjectsPort.countSubjects(assessmentResult.getKitVersionId());
        var advicesResult = loadAdvicesDashboardPort.countAdviceItems(assessmentResult.getId());

        return new Result(buildQuestionsResult(questionsPortResult, progress, evidencesResult),
            buildInsightsResult(insightsResult, assessmentResult.getLastCalculationTime(), attributesCount, subjectsCount),
            buildAdvices(advicesResult)
        );
    }

    private Result.Questions buildQuestionsResult(LoadQuestionsAnswerDashboardPort.Result answerResult, GetAssessmentProgressPort.Result progress, LoadEvidencesDashboardPort.Result evidencesResult) {
        return new Result.Questions(progress.questionsCount(),
            progress.answersCount(),
            progress.questionsCount() - progress.answersCount(),
            answerResult.answers().stream().filter(e -> e.confidence() <= 2).count(),
            progress.questionsCount() -
                evidencesResult.evidences().stream().filter(e -> e.type() != null).map(LoadEvidencesDashboardPort.Result.Evidence::questionId).distinct().count(),
            evidencesResult.evidences().stream().filter(e -> e.type() == null && e.resolved() == null).count());
    }

    private Result.Insights buildInsightsResult(List<LoadInsightsDashboardPort.Result.InsightTime> insightsResult, LocalDateTime lastCalculationTime, int attributesCount, int subjectsCount) {
        int total = attributesCount + subjectsCount + 1;
        var expired = insightsResult.stream().filter(e -> e.insightTime().isBefore(lastCalculationTime)).count();
        return new Result.Insights(total,
            total - insightsResult.size(),
            null,
            expired
        );
    }

    private Result.Advices buildAdvices(CountAdviceItemsPort.Result dashboardAdvicesResult) {
        return new Result.Advices(dashboardAdvicesResult.total());
    }
}
