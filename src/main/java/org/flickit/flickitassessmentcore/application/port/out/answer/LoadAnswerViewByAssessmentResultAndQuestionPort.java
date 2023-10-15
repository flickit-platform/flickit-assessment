package org.flickit.flickitassessmentcore.application.port.out.answer;

import java.util.Optional;
import java.util.UUID;

public interface LoadAnswerViewByAssessmentResultAndQuestionPort {

    Optional<Result> loadView(UUID assessmentResultId, Long questionId);

    record Result(UUID answerId, Long answerOptionId, Boolean isNotApplicable) {
    }
}
