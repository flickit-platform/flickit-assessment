package org.flickit.assessment.core.application.service.answer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.application.domain.notification.SendNotification;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.common.exception.ValidationException;
import org.flickit.assessment.core.application.domain.*;
import org.flickit.assessment.core.application.domain.notification.SubmitAnswerNotificationCmd;
import org.flickit.assessment.core.application.port.in.answer.SubmitAnswerUseCase;
import org.flickit.assessment.core.application.port.out.answer.CreateAnswerPort;
import org.flickit.assessment.core.application.port.out.answer.LoadAnswerPort;
import org.flickit.assessment.core.application.port.out.answer.UpdateAnswerPort;
import org.flickit.assessment.core.application.port.out.answerhistory.CreateAnswerHistoryPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.InvalidateAssessmentResultCalculatePort;
import org.flickit.assessment.core.application.port.out.assessmentresult.InvalidateAssessmentResultConfidencePort;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.core.application.port.out.question.LoadQuestionMayNotBeApplicablePort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.ANSWER_QUESTION;
import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.APPROVE_ANSWER;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.core.application.domain.AnswerStatus.APPROVED;
import static org.flickit.assessment.core.application.domain.AnswerStatus.UNAPPROVED;
import static org.flickit.assessment.core.application.domain.HistoryType.PERSIST;
import static org.flickit.assessment.core.application.domain.HistoryType.UPDATE;
import static org.flickit.assessment.core.common.ErrorMessageKey.SUBMIT_ANSWER_ASSESSMENT_RESULT_NOT_FOUND;
import static org.flickit.assessment.core.common.ErrorMessageKey.SUBMIT_ANSWER_QUESTION_ID_NOT_MAY_NOT_BE_APPLICABLE;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class SubmitAnswerService implements SubmitAnswerUseCase {

    private final LoadAssessmentResultPort loadAssessmentResultPort;
    private final LoadQuestionMayNotBeApplicablePort loadQuestionMayNotBeApplicablePort;
    private final CreateAnswerPort createAnswerPort;
    private final CreateAnswerHistoryPort createAnswerHistoryPort;
    private final LoadAnswerPort loadAnswerPort;
    private final UpdateAnswerPort updateAnswerPort;
    private final InvalidateAssessmentResultCalculatePort invalidateAssessmentResultCalculatePort;
    private final InvalidateAssessmentResultConfidencePort invalidateAssessmentResultConfidencePort;
    private final AssessmentAccessChecker assessmentAccessChecker;

    @Override
    @SendNotification
    public Result submitAnswer(Param param) {
        checkUserAccess(param);

        var assessmentResult = loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())
            .orElseThrow(() -> new ResourceNotFoundException(SUBMIT_ANSWER_ASSESSMENT_RESULT_NOT_FOUND));

        validateQuestionApplicability(param, assessmentResult.getKitVersionId());

        var loadedAnswer = loadAnswerPort.load(assessmentResult.getId(), param.getQuestionId());
        var answerOptionId = Boolean.TRUE.equals(param.getIsNotApplicable()) ? null : param.getAnswerOptionId();
        var confidenceLevelId = resolveConfidenceLevelId(param, answerOptionId);

        return loadedAnswer
            .map(answer -> handleExistingAnswer(assessmentResult, answer, param, answerOptionId, confidenceLevelId))
            .orElseGet(() -> handelNewAnswer(assessmentResult, param, answerOptionId, confidenceLevelId));
    }

    private void checkUserAccess(Param param) {
        if (!assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), ANSWER_QUESTION))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);
    }

    private void validateQuestionApplicability(Param param, long kitVersionId) {
        if (Boolean.TRUE.equals(param.getIsNotApplicable())) {
            var mayNotBeApplicable = loadQuestionMayNotBeApplicablePort.loadMayNotBeApplicableById(param.getQuestionId(), kitVersionId);
            if (!mayNotBeApplicable)
                throw new ValidationException(SUBMIT_ANSWER_QUESTION_ID_NOT_MAY_NOT_BE_APPLICABLE);
        }
    }

    private Integer resolveConfidenceLevelId(Param param, Long answerOptionId) {
        if (answerOptionId != null || Boolean.TRUE.equals(param.getIsNotApplicable()))
            return param.getConfidenceLevelId() == null
                ? ConfidenceLevel.getDefault().getId()
                : param.getConfidenceLevelId();
        return null;
    }

    private Result handleExistingAnswer(AssessmentResult assessmentResult,
                                        Answer loadedAnswer,
                                        Param param,
                                        Long answerOptionId,
                                        Integer confidenceLevelId) {
        var loadedAnswerOptionId = loadedAnswer.getSelectedOption() == null
            ? null
            : loadedAnswer.getSelectedOption().getId();
        var isNotApplicableChanged = !Objects.equals(param.getIsNotApplicable(), loadedAnswer.getIsNotApplicable());
        var isAnswerOptionChanged = Boolean.TRUE.equals(param.getIsNotApplicable())
            ? Boolean.FALSE
            : !Objects.equals(answerOptionId, loadedAnswerOptionId);
        var isConfidenceLevelChanged = !Objects.equals(confidenceLevelId, loadedAnswer.getConfidenceLevelId());
        var loadedAnswerId = loadedAnswer.getId();

        if (!(isNotApplicableChanged || isAnswerOptionChanged || isConfidenceLevelChanged))
            return new NotAffected(loadedAnswerId);

        var status = resolveAnswerStatus(param, answerOptionId);

        updateAnswerPort.update(toUpdateAnswerParam(loadedAnswerId,
            answerOptionId,
            confidenceLevelId,
            param.getIsNotApplicable(),
            status,
            param.getCurrentUserId()));
        createAnswerHistoryPort.persist(toAnswerHistory(loadedAnswerId,
            param,
            assessmentResult.getId(),
            answerOptionId,
            confidenceLevelId,
            status,
            UPDATE));
        invalidateAssessmentResult(assessmentResult, isAnswerOptionChanged, isNotApplicableChanged, isConfidenceLevelChanged);

        log.info("Answer submitted for assessmentId=[{}] with answerId=[{}].", param.getAssessmentId(), loadedAnswerId);
        var notificationCmd = new SubmitAnswerNotificationCmd(param.getAssessmentId(),
            param.getCurrentUserId(),
            hasProgressed(param, loadedAnswer));
        return new Submitted(loadedAnswerId, notificationCmd);
    }

    private AnswerStatus resolveAnswerStatus(Param param, Long answerOptionId) {
        if (answerOptionId != null || Boolean.TRUE.equals(param.getIsNotApplicable()))
            return assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), APPROVE_ANSWER)
                ? APPROVED
                : UNAPPROVED;
        return null;
    }

    private UpdateAnswerPort.Param toUpdateAnswerParam(UUID answerId,
                                                       Long answerOptionId,
                                                       Integer confidenceLevelId,
                                                       Boolean isNotApplicable,
                                                       AnswerStatus status,
                                                       UUID currentUserId) {
        return new UpdateAnswerPort.Param(answerId,
            answerOptionId,
            confidenceLevelId,
            isNotApplicable,
            status,
            currentUserId);
    }

    private void invalidateAssessmentResult(AssessmentResult assessmentResult,
                                            boolean isAnswerOptionChanged,
                                            boolean isNotApplicableChanged,
                                            boolean isConfidenceLevelChanged) {
        if (isAnswerOptionChanged || isNotApplicableChanged)
            invalidateAssessmentResultCalculatePort.invalidateCalculate(assessmentResult.getId());
        if (isConfidenceLevelChanged)
            invalidateAssessmentResultConfidencePort.invalidateConfidence(assessmentResult.getId());
    }

    private boolean hasProgressed(Param param, Answer loadedAnswer) {
        return (!Boolean.TRUE.equals(loadedAnswer.getIsNotApplicable()) && Boolean.TRUE.equals(param.getIsNotApplicable())) ||
            (loadedAnswer.getSelectedOption() == null && param.getAnswerOptionId() != null);
    }

    private Result handelNewAnswer(AssessmentResult assessmentResult,
                                   Param param,
                                   Long answerOptionId,
                                   Integer confidenceLevelId) {
        if (answerOptionId == null && !Boolean.TRUE.equals(param.getIsNotApplicable()))
            return NotAffected.EMPTY;
        var status = assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), APPROVE_ANSWER)
            ? APPROVED
            : UNAPPROVED;
        var createdAnswerId = createAnswer(param, assessmentResult.getId(), answerOptionId, confidenceLevelId, status);
        var notificationCmd = new SubmitAnswerNotificationCmd(param.getAssessmentId(), param.getCurrentUserId(), true);
        log.info("Answer submitted for assessmentId=[{}] with answerId=[{}].", param.getAssessmentId(), createdAnswerId);
        return new Submitted(createdAnswerId, notificationCmd);
    }

    private UUID createAnswer(Param param, UUID assessmentResultId, Long answerOptionId, Integer confidenceLevelId, AnswerStatus status) {
        UUID createdAnswerId = createAnswerPort.persist(toCreateParam(param,
            assessmentResultId,
            answerOptionId,
            confidenceLevelId,
            status));
        createAnswerHistoryPort.persist(toAnswerHistory(createdAnswerId,
            param,
            assessmentResultId,
            answerOptionId,
            confidenceLevelId,
            status,
            PERSIST));
        invalidateAssessmentResultCalculatePort.invalidateCalculate(assessmentResultId);
        invalidateAssessmentResultConfidencePort.invalidateConfidence(assessmentResultId);
        return createdAnswerId;
    }

    private CreateAnswerPort.Param toCreateParam(Param param,
                                                 UUID assessmentResultId,
                                                 Long answerOptionId,
                                                 Integer confidenceLevelId,
                                                 AnswerStatus status) {
        return new CreateAnswerPort.Param(
            assessmentResultId,
            param.getQuestionnaireId(),
            param.getQuestionId(),
            answerOptionId,
            confidenceLevelId,
            param.getIsNotApplicable(),
            status,
            param.getCurrentUserId()
        );
    }

    private AnswerHistory toAnswerHistory(UUID answerId,
                                          Param param,
                                          UUID assessmentResultId,
                                          Long answerOptionId,
                                          Integer confidenceLevelId,
                                          AnswerStatus status,
                                          HistoryType historyType) {
        return new AnswerHistory(null,
            toAnswer(answerId, param, answerOptionId, confidenceLevelId, status),
            assessmentResultId,
            new FullUser(param.getCurrentUserId(), null, null, null),
            LocalDateTime.now(),
            historyType
        );
    }

    private Answer toAnswer(UUID answerId, Param param, Long answerOptionId, Integer confidenceLevelId, AnswerStatus status) {
        return new Answer(
            answerId,
            answerOptionId != null
                ? new AnswerOption(answerOptionId, null, null, null)
                : null,
            param.getQuestionId(),
            confidenceLevelId,
            param.getIsNotApplicable(),
            status);
    }
}
