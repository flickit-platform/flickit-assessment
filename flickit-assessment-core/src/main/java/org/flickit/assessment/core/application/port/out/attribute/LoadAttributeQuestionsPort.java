package org.flickit.assessment.core.application.port.out.attribute;

import org.flickit.assessment.core.application.domain.Answer;
import org.flickit.assessment.core.application.domain.AssessmentResult;
import org.flickit.assessment.core.application.domain.Question;

import java.util.List;
import java.util.UUID;

public interface LoadAttributeQuestionsPort {

    List<Result> loadApplicableQuestions(UUID assessmentId, long attributeId);

    List<Result> loadAttributeMeasureQuestions(AssessmentResult assessmentResult, long attributeId, long measureId);

    record Result(Question question, Answer answer) {
    }
}
