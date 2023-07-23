package org.flickit.flickitassessmentcore.application.port.in.answer;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Value;
import org.flickit.flickitassessmentcore.common.SelfValidating;
import org.flickit.flickitassessmentcore.domain.Answer;

import java.util.List;
import java.util.UUID;

public interface GetAnswerListUseCase {

    Result getAnswerList(Param param);

    @Value
    class Param extends SelfValidating<Param>{

        @NotNull(message = "")
        UUID assessmentId;

        @Size(min = 1, message = "")
        List<Long> questionIds;

        public Param(UUID assessmentId, List<Long> questionIds) {
            this.assessmentId = assessmentId;
            this.questionIds = questionIds;
            this.validateSelf();
        }
    }

    record Result(List<Answer> answers){
    }
}
