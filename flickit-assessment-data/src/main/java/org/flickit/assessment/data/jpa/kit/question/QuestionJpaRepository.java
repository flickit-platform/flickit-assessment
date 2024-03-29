package org.flickit.assessment.data.jpa.kit.question;

import org.flickit.assessment.data.jpa.kit.question.advice.QuestionAdviceView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface QuestionJpaRepository extends JpaRepository<QuestionJpaEntity, Long> {

    List<QuestionJpaEntity> findAllByKitVersionId(@Param("kitVersionId") Long kitVersionId);

    @Modifying
    @Query("""
            UPDATE QuestionJpaEntity q SET
                q.title = :title,
                q.hint = :hint,
                q.index = :index,
                q.mayNotBeApplicable = :mayNotBeApplicable,
                q.advisable = :advisable,
                q.lastModificationTime = :lastModificationTime,
                q.lastModifiedBy = :lastModifiedBy
            WHERE q.id = :id
        """)
    void update(@Param("id") Long id,
                @Param("title") String title,
                @Param("index") Integer index,
                @Param("hint") String hint,
                @Param("mayNotBeApplicable") Boolean mayNotBeApplicable,
                @Param("advisable") Boolean advisable,
                @Param("lastModificationTime") LocalDateTime lastModificationTime,
                @Param("lastModifiedBy") UUID lastModifiedBy);

    @Query("""
            SELECT q as question, qi as questionImpact
            FROM QuestionJpaEntity q
            LEFT JOIN QuestionImpactJpaEntity qi ON q.id = qi.questionId
            WHERE q.questionnaireId IN
                (SELECT qu.id FROM QuestionnaireJpaEntity qu WHERE qu.kitVersionId = :kitVersionId)
        """)
    List<QuestionJoinQuestionImpactView> loadByKitVersionId(@Param("kitVersionId") Long kitVersionId);

    @Query("""
            SELECT DISTINCT q FROM QuestionJpaEntity q
            LEFT JOIN QuestionImpactJpaEntity qi ON q.id = qi.questionId
            LEFT JOIN AttributeJpaEntity at ON qi.attributeId = at.id
            WHERE at.subject.id = :subjectId
        """)
    List<QuestionJpaEntity> findBySubjectId(@Param("subjectId") long subjectId);

    @Query("""
           SELECT DISTINCT q.id AS  questionId,
                anso.index AS answeredOptionIndex,
                qanso.id AS optionId,
                qanso.index AS optionIndex,
                qi.weight AS questionImpactWeight,
                ansoi.value AS optionImpactValue
           FROM QuestionJpaEntity q
           JOIN QuestionnaireJpaEntity qn ON q.questionnaireId = qn.id
           JOIN AssessmentKitJpaEntity kit ON qn.kitVersionId = kit.kitVersionId
           JOIN AssessmentJpaEntity asm ON asm.assessmentKitId = kit.id
           JOIN AssessmentResultJpaEntity asmr ON asm.id = asmr.assessment.id
           JOIN QuestionImpactJpaEntity qi ON q.id = qi.questionId
           JOIN AnswerOptionJpaEntity qanso ON q.id = qanso.questionId
           LEFT JOIN  AnswerOptionImpactJpaEntity ansoi ON qanso.id = ansoi.optionId and qi.id = ansoi.questionImpact.id
           LEFT JOIN AnswerJpaEntity ans ON ans.assessmentResult.id = asmr.id and q.id = ans.questionId
           LEFT JOIN AnswerOptionJpaEntity anso ON ans.answerOptionId = anso.id
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
    List<ImprovableImpactfulQuestionView> findImprovableImpactfulQuestions(UUID assessmentId, Long attributeId, Long maturityLevelId);

    @Query("""
            SELECT
                q.id AS id,
                q.title AS title,
                q.index AS index,
                ao AS option,
                atr AS attribute,
                questionnair AS questionnaire
            FROM QuestionJpaEntity q
            JOIN AnswerOptionJpaEntity ao ON q.id = ao.questionId
            JOIN QuestionnaireJpaEntity questionnair ON q.questionnaireId = questionnair.id
            JOIN AnswerOptionImpactJpaEntity impact ON ao.id = impact.optionId
            JOIN QuestionImpactJpaEntity question_impact ON impact.questionImpact = question_impact
            JOIN AttributeJpaEntity atr ON question_impact.attributeId = atr.id
            WHERE q.id IN :ids
        """)
    List<QuestionAdviceView> findAdviceQuestionsDetail(@Param("ids") List<Long> ids);

    @Query("""
            SELECT q.refNum
            FROM QuestionJpaEntity q
            WHERE q.id = :questionId
        """)
    Optional<UUID> findRefNumById(@Param("questionId") Long questionId);
}
