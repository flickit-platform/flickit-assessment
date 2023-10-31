package org.flickit.assessment.core.test.fixture.adapter.jpa;

import org.flickit.assessment.core.adapter.out.persistence.answer.AnswerJpaEntity;
import org.flickit.assessment.core.adapter.out.persistence.assessmentresult.AssessmentResultJpaEntity;

import java.util.UUID;

public class AnswerJpaEntityMother {

    public static AnswerJpaEntity answerEntityWithOption(AssessmentResultJpaEntity assessmentResultJpaEntity, Long questionId, Long answerOptionId) {
        return new AnswerJpaEntity(
            UUID.randomUUID(),
            assessmentResultJpaEntity,
            1L,
            questionId,
            answerOptionId,
            null
        );
    }

    public static AnswerJpaEntity answerEntityWithNoOption(AssessmentResultJpaEntity assessmentResultJpaEntity, Long questionId) {
        return new AnswerJpaEntity(
            UUID.randomUUID(),
            assessmentResultJpaEntity,
            1L,
            questionId,
            null,
            null
        );
    }

    public static AnswerJpaEntity answerEntityWithIsNotApplicableTrue(AssessmentResultJpaEntity assessmentResultJpaEntity, Long questionId) {
        return new AnswerJpaEntity(
            UUID.randomUUID(),
            assessmentResultJpaEntity,
            1L,
            questionId,
            null,
            Boolean.TRUE
        );
    }
}
