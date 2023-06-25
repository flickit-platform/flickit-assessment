package org.flickit.flickitassessmentcore.application.port.in.answer;

import jakarta.validation.constraints.NotNull;
import lombok.Value;
import org.flickit.flickitassessmentcore.common.SelfValidating;

import java.util.UUID;

import static org.flickit.flickitassessmentcore.common.ErrorMessageKey.*;

public interface SubmitAnswerUseCase {

    UUID submitAnswer(Param command);

    @Value
    class Param extends SelfValidating<Param> {

        @NotNull(message = SUBMIT_ANSWER_ASSESSMENT_RESULT_ID_NOT_NULL)
        UUID assessmentResultId;

        @NotNull(message = SUBMIT_ANSWER_QUESTION_ID_NOT_NULL)
        Long questionId;

        @NotNull(message = SUBMIT_ANSWER_ANSWER_OPTION_ID_NOT_NULL)
        Long answerOptionId;

        public Param(UUID assessmentResultId, Long questionId, Long answerOptionId) {
            this.assessmentResultId = assessmentResultId;
            this.questionId = questionId;
            this.answerOptionId = answerOptionId;
            this.validateSelf();
        }
    }
}
