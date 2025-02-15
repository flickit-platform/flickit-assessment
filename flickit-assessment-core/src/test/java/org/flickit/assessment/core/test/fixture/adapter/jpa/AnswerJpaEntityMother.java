package org.flickit.assessment.core.test.fixture.adapter.jpa;

import org.flickit.assessment.core.application.domain.ConfidenceLevel;
import org.flickit.assessment.data.jpa.core.answer.AnswerJpaEntity;
import org.flickit.assessment.data.jpa.core.assessmentresult.AssessmentResultJpaEntity;

import java.util.UUID;

import static org.flickit.assessment.core.application.domain.AnswerStatus.APPROVED;

public class AnswerJpaEntityMother {

    public static AnswerJpaEntity answerEntityWithOption(AssessmentResultJpaEntity assessmentResultJpaEntity, Long questionId, Long answerOptionId) {
        UUID createdBy = UUID.randomUUID();
        return new AnswerJpaEntity(
            UUID.randomUUID(),
            assessmentResultJpaEntity,
            1L,
            questionId,
            answerOptionId,
            ConfidenceLevel.getDefault().getId(),
            null,
            APPROVED.getId(),
            createdBy,
            createdBy
        );
    }

    public static AnswerJpaEntity answerEntityWithNoOption(AssessmentResultJpaEntity assessmentResultJpaEntity, Long questionId) {
        UUID createdBy = UUID.randomUUID();
        return new AnswerJpaEntity(
            UUID.randomUUID(),
            assessmentResultJpaEntity,
            1L,
            questionId,
            null,
            ConfidenceLevel.getDefault().getId(),
            null,
            APPROVED.getId(),
            createdBy,
            createdBy
        );
    }

    public static AnswerJpaEntity answerEntityWithIsNotApplicableTrue(AssessmentResultJpaEntity assessmentResultJpaEntity, Long questionId) {
        UUID createdBy = UUID.randomUUID();
        return new AnswerJpaEntity(
            UUID.randomUUID(),
            assessmentResultJpaEntity,
            1L,
            questionId,
            null,
            ConfidenceLevel.getDefault().getId(),
            Boolean.TRUE,
            APPROVED.getId(),
            createdBy,
            createdBy
        );
    }
}
