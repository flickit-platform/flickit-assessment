package org.flickit.assessment.core.test.fixture.adapter.jpa;

import org.flickit.assessment.core.application.domain.ConfidenceLevel;
import org.flickit.assessment.data.jpa.core.answer.AnswerJpaEntity;
import org.flickit.assessment.data.jpa.core.assessmentresult.AssessmentResultJpaEntity;

import java.util.UUID;

public class AnswerJpaEntityMother {

    public static AnswerJpaEntity answerEntityWithOption(AssessmentResultJpaEntity assessmentResultJpaEntity, Long questionId, Long answerOptionId) {
        var createdBy = UserJpaEntityMother.user();
        return new AnswerJpaEntity(
            UUID.randomUUID(),
            assessmentResultJpaEntity,
            1L,
            questionId,
            answerOptionId,
            ConfidenceLevel.getDefault().getId(),
            null,
            createdBy,
            createdBy
        );
    }

    public static AnswerJpaEntity answerEntityWithNoOption(AssessmentResultJpaEntity assessmentResultJpaEntity, Long questionId) {
        var createdBy = UserJpaEntityMother.user();
        return new AnswerJpaEntity(
            UUID.randomUUID(),
            assessmentResultJpaEntity,
            1L,
            questionId,
            null,
            ConfidenceLevel.getDefault().getId(),
            null,
            createdBy,
            createdBy
        );
    }

    public static AnswerJpaEntity answerEntityWithIsNotApplicableTrue(AssessmentResultJpaEntity assessmentResultJpaEntity, Long questionId) {
        var createdBy = UserJpaEntityMother.user();
        return new AnswerJpaEntity(
            UUID.randomUUID(),
            assessmentResultJpaEntity,
            1L,
            questionId,
            null,
            ConfidenceLevel.getDefault().getId(),
            Boolean.TRUE,
            createdBy,
            createdBy
        );
    }
}
