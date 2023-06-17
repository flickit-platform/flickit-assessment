package org.flickit.flickitassessmentcore.application.port.in.answer;

import jakarta.validation.constraints.NotNull;
import lombok.Value;
import org.flickit.flickitassessmentcore.common.SelfValidating;

import java.util.UUID;

@Value
public class SubmitAnswerCommand extends SelfValidating<SubmitAnswerCommand> {

    @NotNull
    UUID assessmentResultId;

    @NotNull
    Long questionId;

    @NotNull
    Long answerOptionId;

    public SubmitAnswerCommand(UUID assessmentResultId, Long questionId, Long answerOptionId) {
        this.assessmentResultId = assessmentResultId;
        this.questionId = questionId;
        this.answerOptionId = answerOptionId;
        this.validateSelf();
    }
}
