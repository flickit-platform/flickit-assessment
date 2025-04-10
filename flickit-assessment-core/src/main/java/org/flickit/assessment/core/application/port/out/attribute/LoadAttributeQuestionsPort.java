package org.flickit.assessment.core.application.port.out.attribute;

import org.flickit.assessment.core.application.domain.Answer;
import org.flickit.assessment.core.application.domain.Question;

import java.util.List;
import java.util.UUID;

public interface LoadAttributeQuestionsPort {

    List<Result> loadApplicableQuestions(UUID assessmentId, long attributeId);

    record Result(Question question, Answer answer) {
    }
}
