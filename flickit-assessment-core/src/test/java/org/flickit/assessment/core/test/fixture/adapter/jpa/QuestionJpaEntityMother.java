package org.flickit.assessment.core.test.fixture.adapter.jpa;

import org.flickit.assessment.data.jpa.kit.question.QuestionJpaEntity;

import java.time.LocalDateTime;

public class QuestionJpaEntityMother {

    public static int index = 1;

    public static QuestionJpaEntity questionEntity(Long questionId, Long questionnaireId, boolean mayNotBeApplicable) {
        return new QuestionJpaEntity(
            questionId,
            "code" + questionId,
            "title" + questionId,
            "description" + questionId,
            LocalDateTime.now(),
            LocalDateTime.now(),
            index++,
            questionnaireId,
            mayNotBeApplicable
        );
    }
}
