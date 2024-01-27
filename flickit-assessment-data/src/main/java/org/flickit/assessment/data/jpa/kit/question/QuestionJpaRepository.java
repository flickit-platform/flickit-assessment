package org.flickit.assessment.data.jpa.kit.question;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface QuestionJpaRepository extends JpaRepository<QuestionJpaEntity, Long> {

    @Query("SELECT q FROM QuestionJpaEntity q " +
        "WHERE q.questionnaireId IN (SELECT i.id FROM QuestionnaireJpaEntity i WHERE i.kitId = :kitId)")
    List<QuestionJpaEntity> findByKitId(@Param("kitId") Long kitId);

    @Modifying
    @Query("""
        UPDATE QuestionJpaEntity q SET
        q.title = :title,
        q.hint = :hint,
        q.index = :index,
        q.mayNotBeApplicable = :mayNotBeApplicable,
        q.lastModificationTime = :lastModificationTime,
        q.lastModifiedBy = :lastModifiedBy
        WHERE q.id = :id
        """)
    void update(@Param("id") Long id,
                @Param("title") String title,
                @Param("index") Integer index,
                @Param("hint") String hint,
                @Param("mayNotBeApplicable") Boolean mayNotBeApplicable,
                @Param("lastModificationTime") LocalDateTime lastModificationTime,
                @Param("lastModifiedBy") UUID lastModifiedBy);

    @Query("""
           SELECT DISTINCT q.id AS questionId,
            anso.index AS currentOptionIndex,
            qanso.id AS answerOptionId,
            qanso.index AS answerOptionIndex,
            qi.weight AS questionImpactWeight,
            ansoi.value AS answerOptionImpactValue

           FROM QuestionJpaEntity q
           JOIN QuestionnaireJpaEntity qn
           ON q.questionnaireId = qn.id
           JOIN AssessmentKitJpaEntity kit
           ON qn.kitId = kit.id
           JOIN AssessmentJpaEntity asm
           ON asm.assessmentKitId = kit.id
           JOIN AssessmentResultJpaEntity asmr
           ON asm.id = asmr.assessment.id
           JOIN QuestionImpactJpaEntity qi
           ON q.id = qi.questionId
           JOIN AnswerOptionJpaEntity qanso
           ON q.id = qanso.questionId
           JOIN  AnswerOptionImpactJpaEntity ansoi
           ON qanso.id = ansoi.optionId
           LEFT JOIN AnswerJpaEntity ans
           ON ans.assessmentResult.id = asmr.id and q.id = ans.questionId
           LEFT JOIN AnswerOptionJpaEntity anso
           ON ans.answerOptionId = anso.id
           WHERE (asm.id = :assessmentId
           AND anso.index NOT IN (SELECT MAX(sq_ans.index)
                                  FROM AnswerOptionJpaEntity sq_ans
                                  WHERE sq_ans.questionId = q.id)
           AND qi.attributeId = :attributeId
           AND qi.maturityLevel.id = :maturityLevelId)
           OR (asm.id = :assessmentId
           AND ans.answerOptionId IS NULL
           AND qi.attributeId = :attributeId)
           AND qi.maturityLevel.id = :maturityLevelId
            """)
    List<QuestionView> findAssessedQuestions(UUID assessmentId, Long attributeId, Long maturityLevelId);




}
