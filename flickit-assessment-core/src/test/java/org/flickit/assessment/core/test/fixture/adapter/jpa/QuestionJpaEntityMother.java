package org.flickit.assessment.core.test.fixture.adapter.jpa;

import org.flickit.assessment.data.jpa.kit.question.QuestionJpaEntity;

import java.time.LocalDateTime;
import java.util.UUID;

public class QuestionJpaEntityMother {

    public static int index = 1;

    public static QuestionJpaEntity questionEntity(Long questionId, Long kitVersionId, Long questionnaireId, boolean mayNotBeApplicable, boolean advisable) {
        LocalDateTime creationTime = LocalDateTime.now();
        return new QuestionJpaEntity(
            questionId,
            kitVersionId,
            "code" + questionId,
            index++,
            "title" + questionId,
            "description" + questionId,
            mayNotBeApplicable,
            advisable,
            questionnaireId,
            null,
            creationTime,
            creationTime,
            UUID.randomUUID(),
            UUID.randomUUID()
        );
    }
}
