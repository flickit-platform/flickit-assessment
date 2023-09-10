package org.flickit.flickitassessmentcore.application.port.out.answer;

import java.util.Optional;
import java.util.UUID;

public interface LoadAnswerIdAndOptionIdByAssessmentAndQuestionPort {

    Optional<Result> loadAnswerIdAndOptionId(UUID assessmentId, Long questionId);

    record Result(UUID answerId, Long answerOptionId) {
    }
}
