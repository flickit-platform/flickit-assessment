package org.flickit.flickitassessmentcore.application.port.out.answer;

import java.util.Optional;
import java.util.UUID;

public interface LoadAnswerIdAndIsNotApplicableByAssessmentResultAndQuestionPort {

    Optional<Result> loadAnswerIdAndIsNotApplicable(UUID assessmentResultId, Long questionId);

    record Result(UUID id, Boolean isNotApplicable) {
    }
}
