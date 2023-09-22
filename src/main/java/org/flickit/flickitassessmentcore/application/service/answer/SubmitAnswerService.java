package org.flickit.flickitassessmentcore.application.service.answer;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.application.port.in.answer.SubmitAnswerUseCase;
import org.flickit.flickitassessmentcore.application.port.out.answer.CreateAnswerPort;
import org.flickit.flickitassessmentcore.application.port.out.answer.LoadAnswerPort;
import org.flickit.flickitassessmentcore.application.port.out.answer.UpdateAnswerOptionPort;
import org.flickit.flickitassessmentcore.application.port.out.assessmentresult.InvalidateAssessmentResultPort;
import org.flickit.flickitassessmentcore.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.flickitassessmentcore.application.service.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;

import static org.flickit.flickitassessmentcore.common.ErrorMessageKey.SUBMIT_ANSWER_ASSESSMENT_RESULT_ID_NOT_FOUND;

@Service
@Transactional
@RequiredArgsConstructor
public class SubmitAnswerService implements SubmitAnswerUseCase {

    private final CreateAnswerPort createAnswerPort;
    private final UpdateAnswerOptionPort updateAnswerOptionPort;
    private final LoadAnswerPort loadAnswerPort;
    private final InvalidateAssessmentResultPort invalidateAssessmentResultPort;
    private final LoadAssessmentResultPort loadAssessmentResultPort;

    @Override
    public Result submitAnswer(Param param) {
        UUID assessmentResultId = loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())
            .orElseThrow(() -> new ResourceNotFoundException(SUBMIT_ANSWER_ASSESSMENT_RESULT_ID_NOT_FOUND)).getId();

        CreateOrUpdateResponse response = createOrUpdate(param, assessmentResultId);
        if (response.hasChanged())
            invalidateAssessmentResultPort.invalidateById(assessmentResultId);
        return new Result(response.answerId());
    }

    private CreateOrUpdateResponse createOrUpdate(Param param, UUID assessmentResultId) {
        return loadAnswerPort.loadAnswerIdAndOptionId(assessmentResultId, param.getQuestionId())
            .map(existAnswer -> {
                if (!Objects.equals(param.getAnswerOptionId(), existAnswer.answerOptionId())) { // answer changed
                    updateAnswerOptionPort.updateAnswerOptionById(toUpdateParam(existAnswer.answerId(), param));
                    return new CreateOrUpdateResponse(true, existAnswer.answerId());
                }
                return new CreateOrUpdateResponse(false, existAnswer.answerId());
            }).orElseGet(() -> {
                UUID saveAnswerId = createAnswerPort.persist(toCreateParam(param, assessmentResultId));
                return new CreateOrUpdateResponse(true, saveAnswerId);
            });
    }

    private CreateAnswerPort.Param toCreateParam(Param param, UUID assessmentResultId) {
        return new CreateAnswerPort.Param(
            assessmentResultId,
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
