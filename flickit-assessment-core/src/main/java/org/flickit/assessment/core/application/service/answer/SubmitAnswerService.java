package org.flickit.assessment.core.application.service.answer;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.core.application.port.in.answer.SubmitAnswerUseCase;
import org.flickit.assessment.core.application.port.out.answer.CreateAnswerPort;
import org.flickit.assessment.core.application.port.out.answer.LoadAnswerPort;
import org.flickit.assessment.core.application.port.out.answer.UpdateAnswerPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.InvalidateAssessmentResultPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.core.application.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;

import static org.flickit.assessment.core.common.ErrorMessageKey.SUBMIT_ANSWER_ASSESSMENT_RESULT_NOT_FOUND;

@Service
@Transactional
@RequiredArgsConstructor
public class SubmitAnswerService implements SubmitAnswerUseCase {

    private final LoadAssessmentResultPort loadAssessmentResultPort;
    private final CreateAnswerPort createAnswerPort;
    private final LoadAnswerPort loadAnswerPort;
    private final UpdateAnswerPort updateAnswerPort;
    private final InvalidateAssessmentResultPort invalidateAssessmentResultPort;

    @Override
    public Result submitAnswer(Param param) {
        var assessmentResult = loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())
            .orElseThrow(() -> new ResourceNotFoundException(SUBMIT_ANSWER_ASSESSMENT_RESULT_NOT_FOUND));
        var loadedAnswer = loadAnswerPort.load(assessmentResult.getId(), param.getQuestionId());
        var answerOptionId = Boolean.TRUE.equals(param.getIsNotApplicable()) ? null : param.getAnswerOptionId();
        var confidenceLevelId = Boolean.TRUE.equals(param.getIsNotApplicable()) ? null : param.getConfidenceLevelId();
        if (loadedAnswer.isEmpty()) {
            return saveAnswer(param, assessmentResult.getId(), answerOptionId, confidenceLevelId);
        }
        var loadedAnswerOptionId = loadedAnswer.get().getSelectedOption() == null ? null : loadedAnswer.get().getSelectedOption().getId();
        if (hasNotApplicableChanged(param.getIsNotApplicable(), loadedAnswer.get().getIsNotApplicable())
            || hasAnswerChanged(answerOptionId, loadedAnswerOptionId)
            || hasConfidenceLevelChanged(confidenceLevelId, loadedAnswer.get().getConfidenceLevelId())) {
            updateAnswer(assessmentResult.getId(), loadedAnswer.get().getId(), answerOptionId, confidenceLevelId, param.getIsNotApplicable());
        }
        return new Result(loadedAnswer.get().getId());
    }

    private Result saveAnswer(Param param, UUID assessmentResultId, Long answerOptionId, Integer confidenceLevelId) {
        UUID savedAnswerId = createAnswerPort.persist(toCreateParam(param, assessmentResultId, answerOptionId, confidenceLevelId));
        if (answerOptionId != null || confidenceLevelId != null || Boolean.TRUE.equals(param.getIsNotApplicable())) {
            invalidateAssessmentResultPort.invalidateById(assessmentResultId);
        }
        return new Result(savedAnswerId);
    }

    private CreateAnswerPort.Param toCreateParam(Param param, UUID assessmentResultId, Long answerOptionId, Integer confidenceLevelId) {
        return new CreateAnswerPort.Param(
            assessmentResultId,
            param.getQuestionnaireId(),
            param.getQuestionId(),
            answerOptionId,
            confidenceLevelId,
            param.getIsNotApplicable()
        );
    }

    private boolean hasNotApplicableChanged(Boolean isNotApplicable, Boolean loadedIsNotApplicable) {
        return !Objects.equals(isNotApplicable, loadedIsNotApplicable);
    }

    private boolean hasAnswerChanged(Long answerOptionId, Long loadedAnswerOptionId) {
        return !Objects.equals(answerOptionId, loadedAnswerOptionId);
    }

    private boolean hasConfidenceLevelChanged(Integer confidenceLevelId, Integer loadedConfidenceLevelId) {
        return !Objects.equals(confidenceLevelId, loadedConfidenceLevelId);
    }

    private void updateAnswer(UUID assessmentResultId, UUID loadedAnswerId, Long answerOptionId, Integer confidenceLevelId, Boolean isNotApplicable) {
        updateAnswerPort.update(toUpdateAnswerParam(loadedAnswerId, answerOptionId, confidenceLevelId, isNotApplicable));
        invalidateAssessmentResultPort.invalidateById(assessmentResultId);
    }

    private UpdateAnswerPort.Param toUpdateAnswerParam(UUID answerId, Long answerOptionId, Integer confidenceLevelId, Boolean isNotApplicable) {
        return new UpdateAnswerPort.Param(answerId, answerOptionId, confidenceLevelId, isNotApplicable);
    }

}
