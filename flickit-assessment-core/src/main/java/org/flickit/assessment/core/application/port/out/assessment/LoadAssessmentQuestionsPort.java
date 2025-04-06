package org.flickit.assessment.core.application.port.out.assessment;

import org.flickit.assessment.core.application.domain.Answer;
import org.flickit.assessment.core.application.domain.Question;

import java.util.List;
import java.util.UUID;

public interface LoadAssessmentQuestionsPort {

    List<Result> loadApplicableQuestions(UUID assessmentId);

    record Result(Question question, Answer answer) {
    }
}
