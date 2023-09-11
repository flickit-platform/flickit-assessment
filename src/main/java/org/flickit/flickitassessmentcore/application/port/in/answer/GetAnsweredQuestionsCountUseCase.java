package org.flickit.flickitassessmentcore.application.port.in.answer;

import jakarta.validation.constraints.NotNull;
import lombok.Value;
import org.flickit.flickitassessmentcore.common.SelfValidating;

import java.util.UUID;

import static org.flickit.flickitassessmentcore.common.ErrorMessageKey.GET_ANSWERED_QUESTIONS_COUNT_ASSESSMENT_ID_NOT_NULL;

public interface GetAnsweredQuestionsCountUseCase {

    Result getAnsweredQuestionsCount(Param param);

    @Value
    class Param extends SelfValidating<Param> {

        @NotNull(message = GET_ANSWERED_QUESTIONS_COUNT_ASSESSMENT_ID_NOT_NULL)
        UUID assessmentId;

        public Param(UUID assessmentId) {
            this.assessmentId = assessmentId;
            this.validateSelf();
        }
    }

    record Result(Progress<UUID> assessmentProgress){
    }

    record Progress<K>(K id, Integer allAnswersCount) {
    }
}
