package org.flickit.assessment.core.application.service.answer;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceAlreadyExistsException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.Answer;
import org.flickit.assessment.core.application.domain.AnswerHistory;
import org.flickit.assessment.core.application.domain.FullUser;
import org.flickit.assessment.core.application.port.in.answer.ApproveAnswerUseCase;
import org.flickit.assessment.core.application.port.out.answer.ApproveAnswerPort;
import org.flickit.assessment.core.application.port.out.answer.LoadAnswerPort;
import org.flickit.assessment.core.application.port.out.answerhistory.CreateAnswerHistoryPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.APPROVE_ANSWER;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_ASSESSMENT_RESULT_NOT_FOUND;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.core.application.domain.AnswerStatus.APPROVED;
import static org.flickit.assessment.core.application.domain.HistoryType.UPDATE;
import static org.flickit.assessment.core.common.ErrorMessageKey.APPROVE_ANSWER_ANSWER_ALREADY_APPROVED;
import static org.flickit.assessment.core.common.ErrorMessageKey.APPROVE_ANSWER_QUESTION_NOT_ANSWERED;

@Service
@Transactional
@RequiredArgsConstructor
public class ApproveAnswerService implements ApproveAnswerUseCase {

    private final AssessmentAccessChecker assessmentAccessChecker;
    private final LoadAssessmentResultPort loadAssessmentResultPort;
    private final LoadAnswerPort loadAnswerPort;
    private final ApproveAnswerPort approveAnswerPort;
    private final CreateAnswerHistoryPort createAnswerHistoryPort;

    @Override
    public void approveAnswer(Param param) {
        if (!assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), APPROVE_ANSWER))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        var assessmentResult = loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())
            .orElseThrow(() -> new ResourceNotFoundException(COMMON_ASSESSMENT_RESULT_NOT_FOUND));

        var answer = loadAnswerPort.load(assessmentResult.getId(), param.getQuestionId())
            .orElseThrow(() -> new ResourceNotFoundException(APPROVE_ANSWER_QUESTION_NOT_ANSWERED));
        if (answer.getSelectedOption() == null && !Boolean.TRUE.equals(answer.getIsNotApplicable()))
            throw new ResourceNotFoundException(APPROVE_ANSWER_QUESTION_NOT_ANSWERED);
        if (Objects.equals(answer.getAnswerStatus(), APPROVED))
            throw new ResourceAlreadyExistsException(APPROVE_ANSWER_ANSWER_ALREADY_APPROVED);

        approveAnswerPort.approve(answer.getId(), param.getCurrentUserId());
        createAnswerHistoryPort.persist(toAnswerHistory(answer, param, assessmentResult.getId()));
    }

    private AnswerHistory toAnswerHistory(Answer answer, Param param, UUID assessmentResultId) {
        return new AnswerHistory(null,
            toApprovedAnswer(answer),
            assessmentResultId,
            new FullUser(param.getCurrentUserId(), null, null, null),
            LocalDateTime.now(),
            UPDATE
        );
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
