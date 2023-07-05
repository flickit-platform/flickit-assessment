package org.flickit.flickitassessmentcore.application.service.answer;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.application.port.in.answer.SubmitAnswerIsNotApplicableUseCase;
import org.flickit.flickitassessmentcore.application.port.out.answer.LoadAnswerIdAndIsNotApplicableByAssessmentResultAndQuestionPort;
import org.flickit.flickitassessmentcore.application.port.out.answer.SaveAnswerPort;
import org.flickit.flickitassessmentcore.application.port.out.answer.UpdateAnswerIsNotApplicablePort;
import org.flickit.flickitassessmentcore.application.port.out.assessmentresult.InvalidateAssessmentResultPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class SubmitAnswerIsNotApplicableService implements SubmitAnswerIsNotApplicableUseCase {

    private final SaveAnswerPort saveAnswerPort;
    private final UpdateAnswerIsNotApplicablePort updateAnswerIsNotApplicablePort;
    private final LoadAnswerIdAndIsNotApplicableByAssessmentResultAndQuestionPort loadAnswerIdAndIsNotApplicablePort;
    private final InvalidateAssessmentResultPort invalidateAssessmentResultPort;

    @Override
    public Result submitAnswerIsNotApplicable(Param param) {
        SaveOrUpdateResponse response = saveOrUpdate(param);
        if (response.hasChanged())
            invalidateAssessmentResultPort.invalidateById(param.getAssessmentResultId());
        return new Result(response.answerId());
    }

    private SaveOrUpdateResponse saveOrUpdate(Param param) {
        return loadAnswerIdAndIsNotApplicablePort.loadAnswerIdAndIsNotApplicable(param.getAssessmentResultId(), param.getQuestionId())
            .map(existAnswer -> {
                if (!Objects.equals(existAnswer.isNotApplicable(), param.getIsNotApplicable())) { // answer changed
                    updateAnswerIsNotApplicablePort.updateAnswerIsNotApplicableAndRemoveOptionById(toUpdateParam(existAnswer.id(), param));
                    return new SaveOrUpdateResponse(true, existAnswer.id());
                }
                return new SaveOrUpdateResponse(false, existAnswer.id());
            }).orElseGet(() -> {
                UUID saveAnswerId = saveAnswerPort.persist(toSaveParam(param));
                return new SaveOrUpdateResponse(true, saveAnswerId);
            });
    }

    private SaveAnswerPort.Param toSaveParam(Param param) {
        return new SaveAnswerPort.Param(
            param.getAssessmentResultId(),
            param.getQuestionId(),
            null,
            param.getIsNotApplicable()
        );
    }

    private UpdateAnswerIsNotApplicablePort.Param toUpdateParam(UUID id, Param param) {
        return new UpdateAnswerIsNotApplicablePort.Param(id, param.getIsNotApplicable());
    }

    record SaveOrUpdateResponse(boolean hasChanged, UUID answerId) {
    }
}
