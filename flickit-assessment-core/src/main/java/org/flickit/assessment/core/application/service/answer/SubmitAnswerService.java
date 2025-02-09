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
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
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
        if (!assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), ANSWER_QUESTION))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        var assessmentResult = loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())
            .orElseThrow(() -> new ResourceNotFoundException(SUBMIT_ANSWER_ASSESSMENT_RESULT_NOT_FOUND));

        if (Boolean.TRUE.equals(param.getIsNotApplicable())) {
            var isMayNotBeApplicable = loadQuestionMayNotBeApplicablePort.loadMayNotBeApplicableById(param.getQuestionId(), assessmentResult.getKitVersionId());
            if (!isMayNotBeApplicable)
                throw new ValidationException(SUBMIT_ANSWER_QUESTION_ID_NOT_MAY_NOT_BE_APPLICABLE);
        }

        var loadedAnswer = loadAnswerPort.load(assessmentResult.getId(), param.getQuestionId());
        var answerOptionId = Boolean.TRUE.equals(param.getIsNotApplicable()) ? null : param.getAnswerOptionId();
        Integer confidenceLevelId = param.getConfidenceLevelId() == null ? ConfidenceLevel.getDefault().getId() : param.getConfidenceLevelId();
        confidenceLevelId = (answerOptionId != null || Objects.equals(Boolean.TRUE, param.getIsNotApplicable())) ? confidenceLevelId : null;

        if (loadedAnswer.isEmpty()) {
            if (answerOptionId == null && !Boolean.TRUE.equals(param.getIsNotApplicable()))
                return NotAffected.EMPTY;
            var savedAnswerId = saveAnswer(param, assessmentResult.getId(), answerOptionId, confidenceLevelId);
            var notificationCmd = new SubmitAnswerNotificationCmd(param.getAssessmentId(), param.getCurrentUserId(), true);
            log.info("Answer submitted for assessmentId=[{}] with answerId=[{}].", param.getAssessmentId(), savedAnswerId);
            return new Submitted(savedAnswerId, notificationCmd);
        }

        var loadedAnswerOptionId = loadedAnswer.get().getSelectedOption() == null ? null : loadedAnswer.get().getSelectedOption().getId();

        var isNotApplicableChanged = !Objects.equals(param.getIsNotApplicable(), loadedAnswer.get().getIsNotApplicable());
        var isAnswerOptionChanged = Objects.equals(Boolean.TRUE, param.getIsNotApplicable()) ? Boolean.FALSE : !Objects.equals(answerOptionId, loadedAnswerOptionId);
        var isConfidenceLevelChanged = !Objects.equals(confidenceLevelId, loadedAnswer.get().getConfidenceLevelId());
        var loadedAnswerId = loadedAnswer.get().getId();

        if (!(isNotApplicableChanged || isAnswerOptionChanged || isConfidenceLevelChanged))
            return new NotAffected(loadedAnswerId);

        var updateParam = toUpdateAnswerParam(loadedAnswerId, answerOptionId, confidenceLevelId,
            param.getIsNotApplicable(), param.getCurrentUserId());
        updateAnswerPort.update(updateParam);
        createAnswerHistoryPort.persist(toAnswerHistory(loadedAnswerId, param, assessmentResult.getId(),
            answerOptionId, confidenceLevelId, UPDATE));

        invalidateAssessmentResult(assessmentResult, isAnswerOptionChanged, isNotApplicableChanged, isConfidenceLevelChanged);

        log.info("Answer submitted for assessmentId=[{}] with answerId=[{}].", param.getAssessmentId(), loadedAnswerId);
        var notificationCmd = new SubmitAnswerNotificationCmd(param.getAssessmentId(), param.getCurrentUserId(), hasProgressed(param, loadedAnswer.get()));
        return new Submitted(loadedAnswerId, notificationCmd);
    }

    private static boolean hasProgressed(Param param, Answer loadedAnswer) {
        return (!Boolean.TRUE.equals(loadedAnswer.getIsNotApplicable()) && Boolean.TRUE.equals(param.getIsNotApplicable())) ||
            (loadedAnswer.getSelectedOption() == null && param.getAnswerOptionId() != null);
    }

    private UUID saveAnswer(Param param, UUID assessmentResultId, Long answerOptionId, Integer confidenceLevelId) {
        UUID savedAnswerId = createAnswerPort.persist(toCreateParam(param, assessmentResultId, answerOptionId, confidenceLevelId));
        createAnswerHistoryPort.persist(toAnswerHistory(savedAnswerId, param, assessmentResultId, answerOptionId,
            confidenceLevelId, PERSIST));
        if (answerOptionId != null || confidenceLevelId != null || Boolean.TRUE.equals(param.getIsNotApplicable())) {
            invalidateAssessmentResultCalculatePort.invalidateCalculate(assessmentResultId);
            invalidateAssessmentResultConfidencePort.invalidateConfidence(assessmentResultId);
        }
        return savedAnswerId;
    }

    private CreateAnswerPort.Param toCreateParam(Param param, UUID assessmentResultId, Long answerOptionId, Integer confidenceLevelId) {
        return new CreateAnswerPort.Param(
            assessmentResultId,
            param.getQuestionnaireId(),
            param.getQuestionId(),
            answerOptionId,
            confidenceLevelId,
            param.getIsNotApplicable(),
            param.getCurrentUserId()
        );
    }

    private AnswerHistory toAnswerHistory(UUID answerId,
                                          Param param,
                                          UUID assessmentResultId,
                                          Long answerOptionId,
                                          Integer confidenceLevelId,
                                          HistoryType historyType) {
        return new AnswerHistory(
            null,
            toAnswer(answerId, param, answerOptionId, confidenceLevelId),
            assessmentResultId,
            new FullUser(param.getCurrentUserId(), null, null, null),
            LocalDateTime.now(),
            historyType
        );
    }

    private Answer toAnswer(UUID answerId, Param param, Long answerOptionId, Integer confidenceLevelId) {
        return new Answer(
            answerId,
            answerOptionId != null ?
                new AnswerOption(answerOptionId, null, null, null) :
                null,
            param.getQuestionId(),
            confidenceLevelId,
            param.getIsNotApplicable());
    }

    private UpdateAnswerPort.Param toUpdateAnswerParam(UUID answerId,
                                                       Long answerOptionId,
                                                       Integer confidenceLevelId,
                                                       Boolean isNotApplicable,
                                                       UUID currentUserId) {
        return new UpdateAnswerPort.Param(answerId, answerOptionId, confidenceLevelId, isNotApplicable, currentUserId);
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
}
