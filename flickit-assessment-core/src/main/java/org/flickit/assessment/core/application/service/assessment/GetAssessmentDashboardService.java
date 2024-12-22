package org.flickit.assessment.core.application.service.assessment;

import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.core.application.port.out.answer.LoadQuestionsAnswerDashboardPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

import org.flickit.assessment.core.application.port.in.assessment.GetAssessmentDashboardUseCase;

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

    @Override
    public Result getAssessmentDashboard(Param param) {
        if (!assessmentAccessChecker.isAuthorized(param.getId(), param.getCurrentUserId(), VIEW_DASHBOARD))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        var assessmentResult = loadAssessmentResultPort.loadByAssessmentId(param.getId()).
            orElseThrow(() -> new ResourceNotFoundException(GET_ASSESSMENT_DASHBOARD_ASSESSMENT_RESULT_NOT_FOUND));

        var questionsPortResult = loadQuestionsAnswerDashboardPort.loadQuestionsDashboard(assessmentResult.getKitVersionId());
        var questionsResult = buildQuestionsResult(questionsPortResult);

        return new Result(questionsResult, null, null);
    }

    private Result.Questions buildQuestionsResult(LoadQuestionsAnswerDashboardPort.Result result) {
        return new Result.Questions(result.totalQuestion(),
            result.answers().size(),
            result.totalQuestion() - result.answers().size(),
            result.answers().stream().filter(e -> e.confidence() <= 2).count(),
            result.totalQuestion() -
                result.evidences().stream().filter(e -> e.type() != null).map(LoadQuestionsAnswerDashboardPort.Result.Evidence::questionId).distinct().count(),
            result.evidences().stream().filter(e -> e.resolved() == null).count());
    }
}
