package org.flickit.flickitassessmentcore.application.port.out.answer;

import java.util.Optional;
import java.util.UUID;

public interface LoadAnswerIdAndOptionIdByAssessmentResultAndQuestionPort {

    Optional<Result> loadAnswerIdAndOptionId(UUID assessmentResultId, Long questionId);

    record Result(UUID answerId, Long answerOptionId) {
    }
}
