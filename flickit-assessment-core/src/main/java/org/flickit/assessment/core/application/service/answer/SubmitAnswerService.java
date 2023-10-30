package org.flickit.assessment.core.application.service.answer;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.core.application.port.in.answer.SubmitAnswerUseCase;
import org.flickit.assessment.core.application.port.out.answer.CreateAnswerPort;
import org.flickit.assessment.core.application.port.out.answer.LoadAnswerPort;
import org.flickit.assessment.core.application.port.out.answer.UpdateAnswerPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.InvalidateAssessmentResultPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.core.application.service.exception.ResourceNotFoundException;
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
        if (loadedAnswer.isEmpty()) {
            return saveAnswer(param, assessmentResult.getId(), answerOptionId);
        }
        var loadedAnswerOptionId = loadedAnswer.get().getSelectedOption() == null ? null :  loadedAnswer.get().getSelectedOption().getId();
        if (hasNotApplicableChanged(param.getIsNotApplicable(), loadedAnswer.get().getIsNotApplicable())
            || hasAnswerChanged(answerOptionId, loadedAnswerOptionId)) {
            updateAnswer(assessmentResult.getId(), loadedAnswer.get().getId(), answerOptionId, param.getIsNotApplicable());
        }
        return new Result(loadedAnswer.get().getId());
    }

    private Result saveAnswer(Param param, UUID assessmentResultId, Long answerOptionId) {
        UUID savedAnswerId = createAnswerPort.persist(toCreateParam(param, assessmentResultId, answerOptionId));
        if (answerOptionId != null || Boolean.TRUE.equals(param.getIsNotApplicable())) {
            invalidateAssessmentResultPort.invalidateById(assessmentResultId);
        }
        return new Result(savedAnswerId);
    }

    private CreateAnswerPort.Param toCreateParam(Param param, UUID assessmentResultId, Long answerOptionId) {
        return new CreateAnswerPort.Param(
            assessmentResultId,
            param.getQuestionnaireId(),
            param.getQuestionId(),
            answerOptionId,
            param.getIsNotApplicable()
        );
    }

    private boolean hasNotApplicableChanged(Boolean isNotApplicable, Boolean loadedIsNotApplicable) {
        return !Objects.equals(isNotApplicable, loadedIsNotApplicable);
    }

    private boolean hasAnswerChanged(Long answerOptionId, Long loadedAnswerOptionId) {
        return !Objects.equals(answerOptionId, loadedAnswerOptionId);
    }

    private void updateAnswer(UUID assessmentResultId, UUID loadedAnswerId, Long answerOptionId, Boolean isNotApplicable) {
        updateAnswerPort.update(toUpdateAnswerParam(loadedAnswerId, answerOptionId, isNotApplicable));
        invalidateAssessmentResultPort.invalidateById(assessmentResultId);
    }

    private UpdateAnswerPort.Param toUpdateAnswerParam(UUID answerId, Long answerOptionId, Boolean isNotApplicable) {
        return new UpdateAnswerPort.Param(answerId, answerOptionId, isNotApplicable);
    }

}
