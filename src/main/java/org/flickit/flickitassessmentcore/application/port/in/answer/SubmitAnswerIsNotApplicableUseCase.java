package org.flickit.flickitassessmentcore.application.port.in.answer;

import jakarta.validation.constraints.NotNull;
import lombok.Value;
import org.flickit.flickitassessmentcore.common.SelfValidating;

import java.util.UUID;

import static org.flickit.flickitassessmentcore.common.ErrorMessageKey.*;

public interface SubmitAnswerIsNotApplicableUseCase {

    Result submitAnswerIsNotApplicable(Param param);

    @Value
    class Param extends SelfValidating<Param> {

        @NotNull(message = SUBMIT_ANSWER_ASSESSMENT_RESULT_ID_NOT_NULL)
        UUID assessmentResultId;

        @NotNull(message = SUBMIT_ANSWER_QUESTION_ID_NOT_NULL)
        Long questionId;

        @NotNull(message = SUBMIT_ANSWER_IS_NOT_APPLICABLE_NOT_NULL)
        Boolean isNotApplicable;

        public Param(UUID assessmentResultId, Long questionId, Boolean isNotApplicable) {
            this.assessmentResultId = assessmentResultId;
            this.questionId = questionId;
            this.isNotApplicable = isNotApplicable;
            this.validateSelf();
        }
    }

    record Result(UUID id) {
    }
}
