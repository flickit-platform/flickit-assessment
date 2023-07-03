package org.flickit.flickitassessmentcore.application.service.answer;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.application.port.in.answer.SubmitAnswerIsApplicableUseCase;
import org.flickit.flickitassessmentcore.application.port.out.answer.LoadAnswerIdAndIsApplicableByAssessmentResultAndQuestionPort;
import org.flickit.flickitassessmentcore.application.port.out.answer.SaveAnswerPort;
import org.flickit.flickitassessmentcore.application.port.out.answer.UpdateAnswerIsApplicablePort;
import org.flickit.flickitassessmentcore.application.port.out.assessmentresult.InvalidateAssessmentResultPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class SubmitAnswerIsApplicableService implements SubmitAnswerIsApplicableUseCase {

    private final SaveAnswerPort saveAnswerPort;
    private final UpdateAnswerIsApplicablePort updateAnswerIsApplicablePort;
    private final LoadAnswerIdAndIsApplicableByAssessmentResultAndQuestionPort loadAnswerIdAndIsApplicablePort;
    private final InvalidateAssessmentResultPort invalidateAssessmentResultPort;

    @Override
    public Result submitAnswerIsApplicable(Param param) {
        SaveOrUpdateResponse response = saveOrUpdate(param);
        if (response.hasChanged())
            invalidateAssessmentResultPort.invalidateById(param.getAssessmentResultId());
        return new Result(response.answerId());
    }

    private SaveOrUpdateResponse saveOrUpdate(Param param) {
        return loadAnswerIdAndIsApplicablePort.loadAnswerIdAndIsApplicable(param.getAssessmentResultId(), param.getQuestionId())
            .map(existAnswer -> {
                if (!Objects.equals(existAnswer.isApplicable(), param.getIsApplicable())) { // answer changed
                    updateAnswerIsApplicablePort.updateAnswerIsApplicableAndRemoveOptionById(toUpdateParam(existAnswer.id(), param));
                    return new SaveOrUpdateResponse(true, existAnswer.id());
                }
                return new SaveOrUpdateResponse(false, existAnswer.id());
            }).orElseGet(() -> {
                UUID saveAnswerId = saveAnswerPort.persist(toSaveParam(param));
                // hasChanged is false
                // the not applicable answer doesn't affect the assessment result validation
                // the applicable answer, optionId is null
                return new SaveOrUpdateResponse(false, saveAnswerId);
            });
    }

    private SaveAnswerPort.Param toSaveParam(Param param) {
        return new SaveAnswerPort.Param(
            param.getAssessmentResultId(),
            param.getQuestionId(),
            null,
            param.getIsApplicable()
        );
    }

    private UpdateAnswerIsApplicablePort.Param toUpdateParam(UUID id, Param param) {
        return new UpdateAnswerIsApplicablePort.Param(id, param.getIsApplicable());
    }

    record SaveOrUpdateResponse(boolean hasChanged, UUID answerId) {
    }
}
