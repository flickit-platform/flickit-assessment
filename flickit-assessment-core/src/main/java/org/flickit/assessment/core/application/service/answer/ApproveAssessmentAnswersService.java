package org.flickit.assessment.core.application.service.answer;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.Answer;
import org.flickit.assessment.core.application.domain.AnswerHistory;
import org.flickit.assessment.core.application.domain.AnswerStatus;
import org.flickit.assessment.core.application.domain.FullUser;
import org.flickit.assessment.core.application.port.in.answer.ApproveAssessmentAnswersUseCase;
import org.flickit.assessment.core.application.port.out.answer.ApproveAnswerPort;
import org.flickit.assessment.core.application.port.out.answer.LoadAnswerPort;
import org.flickit.assessment.core.application.port.out.answerhistory.CreateAnswerHistoryPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.APPROVE_ALL_ANSWERS;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_ASSESSMENT_RESULT_NOT_FOUND;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.core.application.domain.AnswerStatus.APPROVED;
import static org.flickit.assessment.core.application.domain.HistoryType.UPDATE;

@Service
@Transactional
@RequiredArgsConstructor
public class ApproveAssessmentAnswersService implements ApproveAssessmentAnswersUseCase {

    private final AssessmentAccessChecker assessmentAccessChecker;
    private final ApproveAnswerPort approveAnswerPort;
    private final LoadAssessmentResultPort loadAssessmentResultPort;
    private final LoadAnswerPort loadAnswerPort;
    private final CreateAnswerHistoryPort createAnswerHistoryPort;

    @Override
    public void approveAllAnswers(Param param) {
        if (!assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), APPROVE_ALL_ANSWERS))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        var assessmentResult = loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())
                .orElseThrow(() -> new ResourceNotFoundException(COMMON_ASSESSMENT_RESULT_NOT_FOUND));

        var answers = loadAnswerPort.loadAll(assessmentResult.getId(), AnswerStatus.UNAPPROVED).stream()
                .filter(ans -> ans.getSelectedOption() != null || Boolean.TRUE.equals(ans.getIsNotApplicable()))
                .toList();

        var answerHistories = answers.stream()
                .map(e -> toAnswerHistory(e, param.getCurrentUserId(), assessmentResult.getId()))
                .toList();

        var answerIds = answers.stream()
            .map(Answer::getId)
            .toList();

        approveAnswerPort.approveAll(answerIds, param.getCurrentUserId());
        createAnswerHistoryPort.persistAll(answerHistories, assessmentResult.getId());
    }

    private AnswerHistory toAnswerHistory(Answer answer, UUID userId, UUID assessmentResultId) {
        return new AnswerHistory(null,
                toApprovedAnswer(answer),
                assessmentResultId,
                new FullUser(userId, null, null, null),
                LocalDateTime.now(),
                UPDATE);
    }

    private Answer toApprovedAnswer(Answer answer) {
        return new Answer(answer.getId(),
                answer.getSelectedOption(),
                answer.getQuestionId(),
                answer.getConfidenceLevelId(),
                answer.getIsNotApplicable(),
                APPROVED);
    }
}
