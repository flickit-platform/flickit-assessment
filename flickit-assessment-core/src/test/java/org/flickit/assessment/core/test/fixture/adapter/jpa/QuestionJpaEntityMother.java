package org.flickit.assessment.core.test.fixture.adapter.jpa;

import org.flickit.assessment.data.jpa.kit.question.QuestionJpaEntity;

import java.time.LocalDateTime;
import java.util.UUID;

public class QuestionJpaEntityMother {

    public static int index = 1;

    public static QuestionJpaEntity questionEntity(Long questionId, Long questionnaireId, boolean mayNotBeApplicable) {
        return new QuestionJpaEntity(
            questionId,
            "code" + questionId,
            index++,
            "title" + questionId,
            "description" + questionId,
            mayNotBeApplicable,
            questionnaireId,
            LocalDateTime.now(),
            LocalDateTime.now(),
            UUID.randomUUID(),
            UUID.randomUUID(),
            1L
        );
    }
}
