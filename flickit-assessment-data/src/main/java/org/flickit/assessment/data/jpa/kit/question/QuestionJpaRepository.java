package org.flickit.assessment.data.jpa.kit.question;

import org.flickit.assessment.data.jpa.kit.question.advice.QuestionAdviceView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface QuestionJpaRepository extends JpaRepository<QuestionJpaEntity, QuestionJpaEntity.EntityId> {

    List<QuestionJpaEntity> findAllByKitVersionId(long kitVersionId);

    Optional<QuestionJpaEntity> findByIdAndKitVersionId(long id,
                                                        long kitVersionId);

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
            WHERE q.id = :id AND q.kitVersionId = :kitVersionId
        """)
    void update(@Param("id") Long id,
                @Param("kitVersionId") Long kitVersionId,
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
            WHERE q.kitVersionId = :kitVersionId
        """)
    List<QuestionJoinQuestionImpactView> loadByKitVersionId(@Param("kitVersionId") Long kitVersionId);

    @Query("""
            SELECT DISTINCT q FROM QuestionJpaEntity q
            LEFT JOIN QuestionImpactJpaEntity qi ON q.id = qi.questionId AND q.kitVersionId = qi.kitVersionId
            LEFT JOIN AttributeJpaEntity at ON qi.attributeId = at.id AND qi.kitVersionId = q.kitVersionId
            WHERE at.subject.id = :subjectId AND at.subject.kitVersionId = :kitVersionId
        """)
    List<QuestionJpaEntity> findBySubjectId(@Param("subjectId") long subjectId, @Param("kitVersionId") Long kitVersionId);

    @Query("""
            SELECT COUNT (DISTINCT q.id) FROM QuestionJpaEntity q
            LEFT JOIN QuestionImpactJpaEntity qi ON q.id = qi.questionId AND q.kitVersionId = qi.kitVersionId
            LEFT JOIN AttributeJpaEntity at ON qi.attributeId = at.id AND qi.kitVersionId = q.kitVersionId
            WHERE at.subject.id = :subjectId AND at.subject.kitVersionId = :kitVersionId
        """)
    Integer countDistinctBySubjectId(@Param("subjectId") long subjectId, @Param("kitVersionId") Long kitVersionId);

    @Query("""
           SELECT DISTINCT q.id AS  questionId,
                anso.index AS answeredOptionIndex,
                qanso.id AS optionId,
                qanso.index AS optionIndex,
                qi.weight AS questionImpactWeight,
                ansoi.value AS optionImpactValue
           FROM QuestionJpaEntity q
           JOIN QuestionnaireJpaEntity qn ON q.questionnaireId = qn.id AND q.kitVersionId = qn.kitVersionId
           JOIN AssessmentResultJpaEntity asmr ON asmr.assessment.id = :assessmentId
           JOIN QuestionImpactJpaEntity qi ON q.id = qi.questionId AND q.kitVersionId = qi.kitVersionId
           JOIN AnswerOptionJpaEntity qanso ON q.id = qanso.questionId AND q.kitVersionId = qanso.kitVersionId
           LEFT JOIN  AnswerOptionImpactJpaEntity ansoi ON qanso.id = ansoi.optionId and qi.id = ansoi.questionImpact.id AND qi.kitVersionId = ansoi.kitVersionId
           LEFT JOIN AnswerJpaEntity ans ON ans.assessmentResult.id = asmr.id and q.id = ans.questionId
           LEFT JOIN AnswerOptionJpaEntity anso ON ans.answerOptionId = anso.id AND q.id = anso.questionId AND q.kitVersionId = anso.kitVersionId
           WHERE (asmr.assessment.id = :assessmentId
               AND anso.index NOT IN (SELECT MAX(sq_ans.index)
                                  FROM AnswerOptionJpaEntity sq_ans
                                  WHERE sq_ans.questionId = q.id)
               AND qi.attributeId = :attributeId
               AND qi.maturityLevel.id = :maturityLevelId)
               OR (asmr.assessment.id = :assessmentId
               AND ans.answerOptionId IS NULL
               AND qi.attributeId = :attributeId)
               AND qi.maturityLevel.id = :maturityLevelId
               AND q.kitVersionId = asmr.kitVersionId
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
            JOIN AnswerOptionJpaEntity ao ON q.id = ao.questionId AND q.kitVersionId = ao.kitVersionId
            JOIN QuestionnaireJpaEntity questionnair ON q.questionnaireId = questionnair.id AND q.kitVersionId = questionnair.kitVersionId
            JOIN AnswerOptionImpactJpaEntity impact ON ao.id = impact.optionId AND ao.kitVersionId = impact.kitVersionId
            JOIN AttributeJpaEntity atr ON impact.questionImpact.attributeId = atr.id AND impact.questionImpact.kitVersionId = atr.subject.kitVersionId
            WHERE q.id IN :ids AND q.kitVersionId = :kitVersionId
        """)
    List<QuestionAdviceView> findAdviceQuestionsDetail(@Param("ids") List<Long> ids, @Param("kitVersionId") Long kitVersionId);

    @Query("""
            SELECT
                MIN(q.index) as index,
                qn.id as questionnaireId
            FROM QuestionJpaEntity q JOIN QuestionnaireJpaEntity qn ON q.questionnaireId = qn.id AND q.kitVersionId = qn.kitVersionId
            JOIN AssessmentResultJpaEntity ar ON ar.kitVersionId = qn.kitVersionId
            WHERE ar.id = :assessmentResultId AND q.id NOT IN (
                SELECT
                    fq.id
                FROM QuestionJpaEntity fq
                JOIN QuestionnaireJpaEntity qsn ON fq.questionnaireId = qsn.id AND fq.kitVersionId = qsn.kitVersionId
                JOIN AnswerJpaEntity ans ON ans.questionId = fq.id
                WHERE ans.assessmentResult.id = :assessmentResultId
                AND (ans.answerOptionId IS NOT NULL OR ans.isNotApplicable = TRUE)
                AND qsn.kitVersionId = ans.assessmentResult.kitVersionId)
            AND qn.id IN (
                SELECT fqn.id
                FROM QuestionnaireJpaEntity fqn
                JOIN AnswerJpaEntity fans ON fans.questionnaireId = fqn.id
                WHERE fans.assessmentResult.id = :assessmentResultId AND qn.kitVersionId = fans.assessmentResult.kitVersionId
            ) GROUP BY qn.id, qn.kitVersionId order by qn.id
    """)
    List<FirstUnansweredQuestionView> findQuestionnairesFirstUnansweredQuestion(@Param("assessmentResultId") UUID assessmentResultId);

    List<QuestionJpaEntity> findAllByQuestionnaireIdAndKitVersionIdOrderByIndexAsc(Long questionnaireId, Long kitVersionId);

    @Query("""
            SELECT
                qr as questionnaire,
                qsn as question,
                qi as questionImpact,
                ov as optionImpact,
                ao as answerOption
            FROM QuestionJpaEntity qsn
            LEFT JOIN AnswerOptionJpaEntity ao on qsn.id = ao.questionId
            LEFT JOIN QuestionnaireJpaEntity qr on qsn.questionnaireId = qr.id
            LEFT JOIN QuestionImpactJpaEntity qi on qsn.id = qi.questionId
            LEFT JOIN AnswerOptionImpactJpaEntity ov on ov.questionImpact.id = qi.id
            WHERE qi.attributeId = :attributeId
                AND qi.maturityLevel.id = :maturityLevelId
            ORDER BY qr.title asc, qsn.index asc
        """)
    List<AttributeLevelImpactfulQuestionsView> findByAttributeIdAndMaturityLevelId(@Param("attributeId") long attributeId,
                                                                                   @Param("maturityLevelId") long maturityLevelId);

    @Query("""
            SELECT COUNT(q)
            FROM QuestionJpaEntity q
            WHERE q.kitVersionId = :kitVersionId
        """)
    int countByKitVersionId(@Param("kitVersionId") long kitVersionId);

    Page<QuestionJpaEntity> findAllByQuestionnaireIdOrderByIndex(@Param("questionnaireId") long questionnaireId, Pageable pageable);
}
