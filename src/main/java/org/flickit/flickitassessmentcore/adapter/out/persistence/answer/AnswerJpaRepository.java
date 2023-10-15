package org.flickit.flickitassessmentcore.adapter.out.persistence.answer;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AnswerJpaRepository extends JpaRepository<AnswerJpaEntity, UUID> {

    Optional<AnswerIdAndOptionIdView> findByAssessmentResultIdAndQuestionId(UUID assessmentResultId, Long questionId);

    List<AnswerJpaEntity> findByAssessmentResultIdAndAnswerOptionIdNotNull(UUID assessmentResultId);

    Page<AnswerJpaEntity> findByAssessmentResultIdAndQuestionnaireIdOrderByQuestionIdAsc(UUID assessmentResultId, Long questionnaireId, Pageable pageable);

    @Query("SELECT COUNT(a) as answerCount FROM AnswerJpaEntity a " +
        "WHERE a.assessmentResult.id=:assessmentResultId AND a.questionId IN :questionIds AND a.answerOptionId IS NOT NULL")
    int getCountByQuestionIds(UUID assessmentResultId, List<Long> questionIds);

    @Query("SELECT COUNT(a) FROM AnswerJpaEntity a where a.assessmentResult.id=:assessmentResultId " +
        "AND a.answerOptionId IS NOT NULL")
    int getCountByAssessmentResultId(UUID assessmentResultId);

    @Query("SELECT a.questionnaireId as questionnaireId, COUNT(a.questionnaireId) as answerCount FROM AnswerJpaEntity a " +
        "where a.assessmentResult.id=:assessmentResultId AND a.answerOptionId IS NOT NULL " +
        "GROUP BY a.questionnaireId")
    List<QuestionnaireIdAndAnswerCountView> getQuestionnairesProgressByAssessmentResultId(UUID assessmentResultId);

    void saveByAnswerOptionIdAndIsNotApplicable(Long answerOptionId, boolean notApplicable);
}
