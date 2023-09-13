package org.flickit.flickitassessmentcore.adapter.out.persistence.answer;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AnswerJpaRepository extends JpaRepository<AnswerJpaEntity, UUID> {

    Optional<AnswerIdAndOptionIdView> findByAssessmentResultIdAndQuestionId(UUID assessmentResultId, Long questionId);

    @Modifying
    @Query("UPDATE AnswerJpaEntity a SET a.answerOptionId=:answerOptionId WHERE a.id=:id")
    void updateAnswerOptionById(UUID id, Long answerOptionId);

    List<AnswerJpaEntity> findByAssessmentResultId(UUID assessmentResultId);

    Page<AnswerJpaEntity> findByAssessmentResultIdAndQuestionnaireIdOrderByQuestionIdAsc(UUID assessmentResultId, Long questionnaireId, Pageable pageable);

    @Query("SELECT COUNT(a) FROM AnswerJpaEntity a where a.assessmentResult.id=:assessmentResultId " +
        "AND a.answerOptionId IS NOT NULL")
    int getCountByAssessmentResult_Id(UUID assessmentResultId);

    @Query("SELECT a.questionnaireId as questionnaireId, COUNT(a.questionnaireId) as answerCount FROM AnswerJpaEntity a " +
        "where a.assessmentResult.id=:assessmentResultId AND a.answerOptionId IS NOT NULL " +
        "GROUP BY a.questionnaireId")
    List<QuestionnaireIdAndAnswerCountView> getCountByAssessmentResult_IdGroupByQuestionnaireId(UUID assessmentResultId);
}
