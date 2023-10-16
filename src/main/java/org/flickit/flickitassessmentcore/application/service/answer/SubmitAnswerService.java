package org.flickit.flickitassessmentcore.application.service.answer;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.application.port.in.answer.SubmitAnswerUseCase;
import org.flickit.flickitassessmentcore.application.port.out.answer.CreateAnswerPort;
import org.flickit.flickitassessmentcore.application.port.out.answer.LoadAnswerPort;
import org.flickit.flickitassessmentcore.application.port.out.answer.UpdateAnswerPort;
import org.flickit.flickitassessmentcore.application.port.out.assessmentresult.InvalidateAssessmentResultPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class SubmitAnswerService implements SubmitAnswerUseCase {

    private final CreateAnswerPort createAnswerPort;
    private final LoadAnswerPort loadAnswerPort;
    private final UpdateAnswerPort updateAnswerPort;
    private final InvalidateAssessmentResultPort invalidateAssessmentResultPort;

    @Override
    public Result submitAnswer(Param param) {
        var loadedAnswer = loadAnswerPort.load(param.getAssessmentResultId(), param.getQuestionId());
        var answerOptionId = param.getIsNotApplicable() ? null : param.getAnswerOptionId();
        if (loadedAnswer.isEmpty()) {
            return saveAnswer(param, answerOptionId);
        }
        if (hasNotApplicableChanged(param.getIsNotApplicable(), loadedAnswer.get().isNotApplicable())
            || hasAnswerChanged(answerOptionId, loadedAnswer.get().answerOptionId())) {
            updateAnswer(param, loadedAnswer.get().answerId(), answerOptionId);
        }
        return new Result(loadedAnswer.get().answerId());
    }

    private Result saveAnswer(Param param, Long answerOptionId) {
        UUID savedAnswerId = createAnswerPort.persist(toCreateParam(param, answerOptionId));
        if (answerOptionId != null || param.getIsNotApplicable()) {
            invalidateAssessmentResultPort.invalidateById(param.getAssessmentResultId());
        }
        return new Result(savedAnswerId);
    }

    private CreateAnswerPort.Param toCreateParam(Param param, Long answerOptionId) {
        return new CreateAnswerPort.Param(
            param.getAssessmentResultId(),
            param.getQuestionnaireId(),
            param.getQuestionId(),
            answerOptionId,
            param.getIsNotApplicable()
        );
    }

    private boolean hasNotApplicableChanged(Boolean isNotApplicable, Boolean loadedIsNotApplicable) {
        return isNotApplicable != loadedIsNotApplicable;
    }

    private boolean hasAnswerChanged(Long answerOptionId, Long loadedAnswerOptionId) {
        return !Objects.equals(answerOptionId, loadedAnswerOptionId);
    }

    private void updateAnswer(Param param, UUID loadedAnswerId, Long answerOptionId) {
        updateAnswerPort.update(toUpdateAnswerParam(loadedAnswerId, answerOptionId, param.getIsNotApplicable()));
        invalidateAssessmentResultPort.invalidateById(param.getAssessmentResultId());
    }

    private UpdateAnswerPort.Param toUpdateAnswerParam(UUID answerId, Long answerOptionId, Boolean isNotApplicable) {
        return new UpdateAnswerPort.Param(answerId, answerOptionId, isNotApplicable);
    }

}
