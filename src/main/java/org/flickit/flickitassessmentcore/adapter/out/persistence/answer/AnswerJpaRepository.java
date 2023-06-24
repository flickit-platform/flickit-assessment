package org.flickit.flickitassessmentcore.adapter.out.persistence.answer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface AnswerJpaRepository extends JpaRepository<AnswerJpaEntity, UUID> {

    boolean existsByAssessmentResult_IdAndQuestionId(UUID assessmentResultId, Long questionId);

    AnswerJpaEntity findByAssessmentResult_IdAndQuestionId(UUID assessmentResultId, Long questionId);

    @Query("UPDATE AnswerJpaEntity a SET a.answerOptionId=:answerOptionId WHERE a.id=:id")
    void updateAnswerOptionById(UUID id, Long answerOptionId);
}
