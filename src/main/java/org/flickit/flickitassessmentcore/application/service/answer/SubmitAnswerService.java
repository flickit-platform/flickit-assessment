package org.flickit.flickitassessmentcore.application.service.answer;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.application.port.in.answer.SubmitAnswerUseCase;
import org.flickit.flickitassessmentcore.application.port.out.answer.LoadAnswerIdAndOptionIdByAssessmentResultAndQuestionPort;
import org.flickit.flickitassessmentcore.application.port.out.answer.SaveAnswerPort;
import org.flickit.flickitassessmentcore.application.port.out.answer.UpdateAnswerOptionPort;
import org.flickit.flickitassessmentcore.application.port.out.assessmentresult.InvalidateAssessmentResultPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class SubmitAnswerService implements SubmitAnswerUseCase {

    private final SaveAnswerPort saveAnswerPort;
    private final UpdateAnswerOptionPort updateAnswerOptionPort;
    private final LoadAnswerIdAndOptionIdByAssessmentResultAndQuestionPort loadAnswerIdAndOptionIdPort;
    private final InvalidateAssessmentResultPort invalidateAssessmentResultPort;

    @Override
    public Result submitAnswer(Param param) {
        SaveOrUpdateResponse response = saveOrUpdate(param);
        if (response.hasChanged())
            invalidateAssessmentResultPort.invalidateById(param.getAssessmentResultId());
        return new Result(response.answerId());
    }

    private SaveOrUpdateResponse saveOrUpdate(Param param) {
        return loadAnswerIdAndOptionIdPort.loadAnswerIdAndOptionId(param.getAssessmentResultId(), param.getQuestionId())
            .map(existAnswer -> {
                if (!Objects.equals(param.getAnswerOptionId(), existAnswer.answerOptionId())) { // answer changed
                    updateAnswerOptionPort.updateAnswerOptionById(toUpdateParam(existAnswer.answerId(), param));
                    return new SaveOrUpdateResponse(true, existAnswer.answerId());
                }
                return new SaveOrUpdateResponse(false, existAnswer.answerId());
            }).orElseGet(() -> {
                UUID saveAnswerId = saveAnswerPort.persist(toSaveParam(param));
                return new SaveOrUpdateResponse(true, saveAnswerId);
            });
    }

    private SaveAnswerPort.Param toSaveParam(Param param) {
        return new SaveAnswerPort.Param(
            param.getAssessmentResultId(),
            param.getQuestionId(),
            param.getAnswerOptionId(),
            true
        );
    }

    private UpdateAnswerOptionPort.Param toUpdateParam(UUID id, Param param) {
        return new UpdateAnswerOptionPort.Param(id, param.getAnswerOptionId());
    }

    record SaveOrUpdateResponse(boolean hasChanged, UUID answerId) {
    }
}
