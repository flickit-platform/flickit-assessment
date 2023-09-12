package org.flickit.flickitassessmentcore.application.service.answer;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.application.port.in.answer.SubmitAnswerUseCase;
import org.flickit.flickitassessmentcore.application.port.out.answer.CreateAnswerPort;
import org.flickit.flickitassessmentcore.application.port.out.answer.LoadAnswerPort;
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

    private final CreateAnswerPort createAnswerPort;
    private final UpdateAnswerOptionPort updateAnswerOptionPort;
    private final LoadAnswerPort loadAnswerIdAndOptionIdPort;
    private final InvalidateAssessmentResultPort invalidateAssessmentResultPort;

    @Override
    public Result submitAnswer(Param param) {
        CreateOrUpdateResponse response = createOrUpdate(param);
        if (response.hasChanged())
            invalidateAssessmentResultPort.invalidateByAssessmentId(param.getAssessmentId());
        return new Result(response.answerId());
    }

    private CreateOrUpdateResponse createOrUpdate(Param param) {
        return loadAnswerIdAndOptionIdPort.loadAnswerIdAndOptionId(param.getAssessmentId(), param.getQuestionId())
            .map(existAnswer -> {
                if (!Objects.equals(param.getAnswerOptionId(), existAnswer.answerOptionId())) { // answer changed
                    updateAnswerOptionPort.updateAnswerOptionById(toUpdateParam(existAnswer.answerId(), param));
                    return new CreateOrUpdateResponse(true, existAnswer.answerId());
                }
                return new CreateOrUpdateResponse(false, existAnswer.answerId());
            }).orElseGet(() -> {
                UUID saveAnswerId = createAnswerPort.persist(toCreateParam(param));
                return new CreateOrUpdateResponse(true, saveAnswerId);
            });
    }

    private CreateAnswerPort.Param toCreateParam(Param param) {
        return new CreateAnswerPort.Param(
            param.getAssessmentId(),
            param.getQuestionnaireId(),
            param.getQuestionId(),
            param.getAnswerOptionId()
        );
    }

    private UpdateAnswerOptionPort.Param toUpdateParam(UUID id, Param param) {
        return new UpdateAnswerOptionPort.Param(id, param.getAnswerOptionId());
    }

    record CreateOrUpdateResponse(boolean hasChanged, UUID answerId) {
    }
}
