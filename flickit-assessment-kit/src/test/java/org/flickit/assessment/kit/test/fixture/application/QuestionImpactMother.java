package org.flickit.assessment.kit.test.fixture.application;

import org.flickit.assessment.kit.application.domain.QuestionImpact;

public class QuestionImpactMother {

    private static Long id = 1340L;

    public static QuestionImpact createQuestionImpact(Long attributeId, Long maturityLevelId, int weight, Long questionId) {
        return new QuestionImpact(
            id++,
            attributeId,
            maturityLevelId,
            weight,
            questionId
        );
    }
}
