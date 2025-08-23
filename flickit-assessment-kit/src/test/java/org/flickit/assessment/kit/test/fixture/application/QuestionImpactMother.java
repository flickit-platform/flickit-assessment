package org.flickit.assessment.kit.test.fixture.application;

import org.flickit.assessment.kit.application.domain.QuestionImpact;

import java.time.LocalDateTime;
import java.util.UUID;

public class QuestionImpactMother {

    private static Long id = 1340L;

    public static QuestionImpact createQuestionImpact(Long attributeId, Long maturityLevelId, int weight, Long questionId) {
        return new QuestionImpact(
            id++,
            attributeId,
            maturityLevelId,
            weight,
            302L,
            questionId,
            LocalDateTime.now(),
            LocalDateTime.now(),
            UUID.randomUUID(),
            UUID.randomUUID()
        );
    }
}
