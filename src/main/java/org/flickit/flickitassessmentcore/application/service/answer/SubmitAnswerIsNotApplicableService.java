package org.flickit.flickitassessmentcore.application.service.answer;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.application.port.in.answer.SubmitAnswerIsNotApplicableUseCase;
import org.flickit.flickitassessmentcore.application.port.out.answer.LoadAnswerIdAndIsNotApplicableByAssessmentResultAndQuestionPort;
import org.flickit.flickitassessmentcore.application.port.out.answer.CreateAnswerPort;
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

    private final CreateAnswerPort createAnswerPort;
    private final UpdateAnswerIsNotApplicablePort updateAnswerIsNotApplicablePort;
    private final LoadAnswerIdAndIsNotApplicableByAssessmentResultAndQuestionPort loadAnswerIdAndIsNotApplicablePort;
    private final InvalidateAssessmentResultPort invalidateAssessmentResultPort;

    @Override
    public Result submitAnswerIsNotApplicable(Param param) {
        CreateOrUpdateResponse response = createOrUpdate(param);
        if (response.hasChanged())
            invalidateAssessmentResultPort.invalidateById(param.getAssessmentResultId());
        return new Result(response.answerId());
    }

    private CreateOrUpdateResponse createOrUpdate(Param param) {
        return loadAnswerIdAndIsNotApplicablePort.loadAnswerIdAndIsNotApplicable(param.getAssessmentResultId(), param.getQuestionId())
            .map(existAnswer -> {
                if (!Objects.equals(existAnswer.isNotApplicable(), param.getIsNotApplicable())) { // answer changed
                    updateAnswerIsNotApplicablePort.updateAnswerIsNotApplicableAndRemoveOptionById(toUpdateParam(existAnswer.id(), param));
                    return new CreateOrUpdateResponse(true, existAnswer.id());
                }
                return new CreateOrUpdateResponse(false, existAnswer.id());
            }).orElseGet(() -> {
                UUID saveAnswerId = createAnswerPort.persist(toCreateParam(param));
                return new CreateOrUpdateResponse(true, saveAnswerId);
            });
    }

    private CreateAnswerPort.Param toCreateParam(Param param) {
        return new CreateAnswerPort.Param(
            param.getAssessmentResultId(),
            param.getQuestionnaireId(),
            param.getQuestionId(),
            null,
            param.getIsNotApplicable()
        );
    }

    private UpdateAnswerIsNotApplicablePort.Param toUpdateParam(UUID id, Param param) {
        return new UpdateAnswerIsNotApplicablePort.Param(id, param.getIsNotApplicable());
    }

    record CreateOrUpdateResponse(boolean hasChanged, UUID answerId) {
    }
}
