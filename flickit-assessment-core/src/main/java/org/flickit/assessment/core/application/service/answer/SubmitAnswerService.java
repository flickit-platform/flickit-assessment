package org.flickit.assessment.core.application.service.answer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.application.domain.notification.SendNotification;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.common.exception.ValidationException;
import org.flickit.assessment.core.application.domain.*;
import org.flickit.assessment.core.application.port.in.answer.SubmitAnswerUseCase;
import org.flickit.assessment.core.application.port.out.answer.CreateAnswerPort;
import org.flickit.assessment.core.application.port.out.answer.LoadAnswerPort;
import org.flickit.assessment.core.application.port.out.answer.UpdateAnswerPort;
import org.flickit.assessment.core.application.port.out.answerhistory.CreateAnswerHistoryPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.InvalidateAssessmentResultPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.core.application.port.out.question.LoadQuestionMayNotBeApplicablePort;
import org.flickit.assessment.core.application.service.answer.notification.SubmitAnswerNotificationCmd;
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
    private final InvalidateAssessmentResultPort invalidateAssessmentResultPort;
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

        if (loadedAnswer.isEmpty())
            return saveAnswer(param, assessmentResult, answerOptionId, confidenceLevelId);

        var loadedAnswerOptionId = loadedAnswer.get().getSelectedOption() == null ? null : loadedAnswer.get().getSelectedOption().getId();

        var isNotApplicableChanged = !Objects.equals(param.getIsNotApplicable(), loadedAnswer.get().getIsNotApplicable());
        var isAnswerOptionChanged = Objects.equals(Boolean.TRUE, param.getIsNotApplicable()) ? Boolean.FALSE : !Objects.equals(answerOptionId, loadedAnswerOptionId);
        var isConfidenceLevelChanged = !Objects.equals(confidenceLevelId, loadedAnswer.get().getConfidenceLevelId());
        var notificationCmd = new SubmitAnswerNotificationCmd(assessmentResult.getAssessment().getCreatedBy(), param.getAssessmentId(), param.getCurrentUserId());

        if (isNotApplicableChanged || isAnswerOptionChanged || isConfidenceLevelChanged) {
            var updateParam = toUpdateAnswerParam(loadedAnswer.get().getId(), answerOptionId, confidenceLevelId,
                param.getIsNotApplicable(), param.getCurrentUserId());
            var isCalculateValid = !isAnswerOptionChanged && !isNotApplicableChanged;
            updateAnswerPort.update(updateParam);
            createAnswerHistoryPort.persist(toAnswerHistory(loadedAnswer.get().getId(), param, assessmentResult.getId(),
                answerOptionId, confidenceLevelId, UPDATE));
            invalidateAssessmentResultPort.invalidateById(assessmentResult.getId(), isCalculateValid, !isConfidenceLevelChanged);
        }

        log.info("Answer submitted for assessmentId=[{}] with answerId=[{}].", param.getAssessmentId(), loadedAnswer.get().getId());

        return new Result(loadedAnswer.get().getId(), notificationCmd);
    }

    private Result saveAnswer(Param param, AssessmentResult assessmentResult, Long answerOptionId, Integer confidenceLevelId) {
        var notificationCmd = new SubmitAnswerNotificationCmd(assessmentResult.getAssessment().getCreatedBy(), param.getAssessmentId(), param.getCurrentUserId());
        var assessmentResultId = assessmentResult.getId();
        if (answerOptionId == null && !Boolean.TRUE.equals(param.getIsNotApplicable())) {
            return new Result(null, new SubmitAnswerNotificationCmd(null, null, null));
        }
        UUID savedAnswerId = createAnswerPort.persist(toCreateParam(param, assessmentResultId, answerOptionId, confidenceLevelId));
        createAnswerHistoryPort.persist(toAnswerHistory(savedAnswerId, param, assessmentResultId, answerOptionId,
            confidenceLevelId, PERSIST));
        if (answerOptionId != null || confidenceLevelId != null || Boolean.TRUE.equals(param.getIsNotApplicable())) {
            invalidateAssessmentResultPort.invalidateById(assessmentResultId, Boolean.FALSE, Boolean.FALSE);
        }
        return new Result(savedAnswerId, notificationCmd);
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
                new AnswerOption(answerOptionId, null, null, param.getQuestionId(), null) :
                null,
            param.getQuestionId(),
            confidenceLevelId,
            param.getIsNotApplicable());
    }

    private UpdateAnswerPort.Param toUpdateAnswerParam(UUID answerId, Long answerOptionId, Integer confidenceLevelId,
                                                       Boolean isNotApplicable, UUID currentUserId) {
        return new UpdateAnswerPort.Param(answerId, answerOptionId, confidenceLevelId, isNotApplicable, currentUserId);
    }
}
