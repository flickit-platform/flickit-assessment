package org.flickit.flickitassessmentcore.application.service.answer;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.application.port.in.answer.SubmitAnswerUseCase;
import org.flickit.flickitassessmentcore.application.port.out.answer.CreateAnswerPort;
import org.flickit.flickitassessmentcore.application.port.out.answer.LoadAnswerViewByAssessmentResultAndQuestionPort;
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
    private final UpdateAnswerPort updateAnswerPort;
    private final LoadAnswerViewByAssessmentResultAndQuestionPort loadAnswerViewPort;
    private final InvalidateAssessmentResultPort invalidateAssessmentResultPort;

    @Override
    public Result submitAnswer(Param param) {
        var loadedAnswer = loadAnswerViewPort.loadView(param.getAssessmentResultId(), param.getQuestionId());
        var answerOptionId = param.getIsNotApplicable() ? null : param.getAnswerOptionId();
        if (loadedAnswer.isEmpty()) {
            UUID savedAnswerId = createAnswerPort.persist(toCreateParam(param, answerOptionId));
            invalidateAssessmentResultPort.invalidateById(param.getAssessmentResultId());
            return new Result(savedAnswerId);
        }
        if (hasNotApplicableChanged(param.getIsNotApplicable(), loadedAnswer.get().isNotApplicable())
            || hasAnswerChanged(answerOptionId, loadedAnswer.get().answerOptionId())) {
            updateAnswerPort.update(toUpdateAnswerParam(answerOptionId, param.getIsNotApplicable()));
            invalidateAssessmentResultPort.invalidateById(param.getAssessmentResultId());
        }
        return new Result(loadedAnswer.get().answerId());
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

    private UpdateAnswerPort.Param toUpdateAnswerParam(Long answerOptionId, Boolean isNotApplicable) {
        return new UpdateAnswerPort.Param(answerOptionId, isNotApplicable);
    }

}
