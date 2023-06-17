package org.flickit.flickitassessmentcore.application.service.answer;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.application.port.in.answer.SubmitAnswerCommand;
import org.flickit.flickitassessmentcore.application.port.in.answer.SubmitAnswerUseCase;
import org.flickit.flickitassessmentcore.application.port.out.answer.SaveAnswerPort;
import org.flickit.flickitassessmentcore.application.port.out.assessmentresult.CheckAssessmentResultExistencePort;
import org.flickit.flickitassessmentcore.application.port.out.assessmentresult.InvalidateAssessmentResultPort;
import org.flickit.flickitassessmentcore.application.service.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.flickit.flickitassessmentcore.common.ErrorMessageKey.SUBMIT_ANSWER_ASSESSMENT_RESULT_ID_NOT_FOUND_MESSAGE;

@Service
@RequiredArgsConstructor
@Transactional
public class SubmitAnswerService implements SubmitAnswerUseCase {

    private final SaveAnswerPort saveAnswerPort;
    private final InvalidateAssessmentResultPort invalidateAssessmentResultPort;
    private final CheckAssessmentResultExistencePort assessmentResultExistencePort;

    @Override
    public UUID submitAnswer(SubmitAnswerCommand command) {
        validateCommand(command);
        SaveAnswerPort.Param param = toParam(command);
        UUID answerId = saveAnswerPort.persist(param);
        afterSave(param);
        return answerId;
    }

    private void validateCommand(SubmitAnswerCommand command) {
        checkAssessmentResultExistence(command.getAssessmentResultId());
    }

    private void afterSave(SaveAnswerPort.Param param) {
        invalidateAssessmentResult(param.assessmentResultId());
    }

    private void checkAssessmentResultExistence(UUID assessmentResultId) {
        if (!assessmentResultExistencePort.existsById(assessmentResultId))
            throw new ResourceNotFoundException(SUBMIT_ANSWER_ASSESSMENT_RESULT_ID_NOT_FOUND_MESSAGE);
    }

    private void invalidateAssessmentResult(UUID assessmentResultId) {
        invalidateAssessmentResultPort.invalidateAssessmentResultById(assessmentResultId);
    }

    private SaveAnswerPort.Param toParam(SubmitAnswerCommand command) {
        return new SaveAnswerPort.Param(
            command.getAssessmentResultId(),
            command.getQuestionId(),
            command.getAnswerOptionId()
        );
    }
}
