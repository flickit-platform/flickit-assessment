package org.flickit.flickitassessmentcore.application.port.out.answer;

import java.util.Optional;
import java.util.UUID;

public interface LoadAnswerPort {

    Optional<Result> load(UUID assessmentResultId, Long questionId);

    record Result(UUID answerId, Long answerOptionId, Boolean isNotApplicable) {
    }
}
