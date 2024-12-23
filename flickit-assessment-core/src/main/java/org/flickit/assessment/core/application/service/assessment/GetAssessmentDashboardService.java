package org.flickit.assessment.core.application.service.assessment;

import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.assessmentdashboard.DashboardAdvices;
import org.flickit.assessment.core.application.domain.assessmentdashboard.DashboardEvidences;
import org.flickit.assessment.core.application.domain.assessmentdashboard.DashboardInsights;
import org.flickit.assessment.core.application.domain.assessmentdashboard.DashboardAnswersQuestions;
import org.flickit.assessment.core.application.port.out.advice.LoadAdvicesDashboardPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.core.application.port.out.answer.LoadQuestionsAnswerDashboardPort;
import org.flickit.assessment.core.application.port.out.attributeinsight.LoadInsightsDashboardPort;
import org.flickit.assessment.core.application.port.out.evidence.LoadEvidencesDashboardPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

import org.flickit.assessment.core.application.port.in.assessment.GetAssessmentDashboardUseCase;

import java.time.LocalDateTime;

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
    private final LoadAdvicesDashboardPort loadAdvicesDashboardPort;
    private final LoadEvidencesDashboardPort loadEvidencesDashboardPort;

    @Override
    public Result getAssessmentDashboard(Param param) {
        if (!assessmentAccessChecker.isAuthorized(param.getId(), param.getCurrentUserId(), VIEW_DASHBOARD))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        var assessmentResult = loadAssessmentResultPort.loadByAssessmentId(param.getId()).
            orElseThrow(() -> new ResourceNotFoundException(GET_ASSESSMENT_DASHBOARD_ASSESSMENT_RESULT_NOT_FOUND));

        var questionsPortResult = loadQuestionsAnswerDashboardPort.loadQuestionsDashboard(param.getId(), assessmentResult.getKitVersionId());
        var evidencesResult = loadEvidencesDashboardPort.loadEvidencesDashboard(param.getId());
        var insightsResult = loadInsightsDashboardPort.loadInsights(assessmentResult.getKitVersionId());
        var advicesResult = loadAdvicesDashboardPort.loadAdviceDashboard();

        return new Result(buildQuestionsResult(questionsPortResult, evidencesResult),
            buildInsightsResult(insightsResult, assessmentResult.getLastCalculationTime()),
            buildAdvices(advicesResult));
    }

    private Result.Questions buildQuestionsResult(DashboardAnswersQuestions answerResult, DashboardEvidences evidencesResult) {
        return new Result.Questions(answerResult.totalQuestion(),
            answerResult.answers().size(),
            answerResult.totalQuestion() - answerResult.answers().size(),
            answerResult.answers().stream().filter(e -> e.confidence() <= 2).count(),
            answerResult.totalQuestion() -
                evidencesResult.evidences().stream().filter(e -> e.type() != null).map(DashboardEvidences.Evidence::questionId).distinct().count(),
            evidencesResult.evidences().stream().filter(e -> e.resolved() == null).count());
    }

    private Result.Insights buildInsightsResult(DashboardInsights result, LocalDateTime lastCalculationTime) {
        long total = result.attributesCount() + result.subjectsCount() + 1;
        var expired = result.insights().stream().filter(e -> e.insightTime().isBefore(lastCalculationTime)).count();
        return new Result.Insights(total,
            total - result.insights().size(),
            null,
            expired
        );
    }

    private Result.Advices buildAdvices(DashboardAdvices dashboardAdvicesResult) {
        return new Result.Advices(dashboardAdvicesResult.total());
    }
}
