package org.flickit.assessment.core.test.fixture.adapter.jpa;

import org.flickit.assessment.data.jpa.kit.questionimpact.QuestionImpactJpaEntity;

import java.time.LocalDateTime;
import java.util.UUID;

public class QuestionImpactEntityMother {

    private static long questionImpactId = 134L;

    public static QuestionImpactJpaEntity questionImpactEntity(Long maturityLevelId, Long questionId, Long attributeId) {
        return new QuestionImpactJpaEntity(
            questionImpactId++,
            1,
            123L,
            questionId,
            attributeId,
            maturityLevelId,
            LocalDateTime.now(),
            LocalDateTime.now(),
            UUID.randomUUID(),
            UUID.randomUUID()
        );
    }
}
