package org.flickit.assessment.kit.application.port.out.question;

import org.flickit.assessment.kit.application.domain.Question;
import org.flickit.assessment.kit.application.domain.Questionnaire;

import java.util.List;

public interface LoadAttributeLevelQuestionsPort {

    List<Result> loadAttributeLevelQuestions(long kitVersionId, long attributeId, long maturityLevelId);

    record Result(Question question, Questionnaire questionnaire) {
    }
}
