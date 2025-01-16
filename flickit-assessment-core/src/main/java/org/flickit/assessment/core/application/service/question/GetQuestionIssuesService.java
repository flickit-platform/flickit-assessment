package org.flickit.assessment.core.application.service.question;

import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.Answer;
import org.flickit.assessment.core.application.domain.ConfidenceLevel;
import org.flickit.assessment.core.application.port.in.questions.GetQuestionIssuesUseCase;
import org.flickit.assessment.core.application.port.out.answer.LoadAnswerPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.core.application.port.out.evidence.CountEvidencesPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.VIEW_DASHBOARD;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.core.common.ErrorMessageKey.GET_QUESTION_ISSUES_ASSESSMENT_RESULT_NOT_FOUND;

@Service
@Transactional
@RequiredArgsConstructor
public class GetQuestionIssuesService implements GetQuestionIssuesUseCase {

    private final AssessmentAccessChecker assessmentAccessChecker;
    private final LoadAssessmentResultPort loadAssessmentResultPort;
    private final LoadAnswerPort loadAnswerPort;
    private final CountEvidencesPort countEvidencesPort;

    @Override
    public Result getQuestionIssues(Param param) {
        if (!assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_DASHBOARD))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        var assessmentResult = loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())
            .orElseThrow(() -> new ResourceNotFoundException(GET_QUESTION_ISSUES_ASSESSMENT_RESULT_NOT_FOUND));

        var answer = loadAnswerPort.load(assessmentResult.getId(), param.getQuestionId()).orElse(null);
        var isAnswered = hasAnswer(answer);
        return new Result(!isAnswered,
            isAnswered && (answer.getConfidenceLevelId() < ConfidenceLevel.SOMEWHAT_UNSURE.getId()),
            isAnswered && countEvidencesPort.countQuestionEvidences(param.getAssessmentId(), param.getQuestionId()) == 0,
            isAnswered ? countEvidencesPort.countQuestionUnresolvedComments(param.getAssessmentId(), param.getQuestionId()) : 0);
    }

    private boolean hasAnswer(Answer answer) {
        if (answer == null)
            return false;
        return answer.getSelectedOption() != null || Boolean.TRUE.equals(answer.getIsNotApplicable());
    }
}
