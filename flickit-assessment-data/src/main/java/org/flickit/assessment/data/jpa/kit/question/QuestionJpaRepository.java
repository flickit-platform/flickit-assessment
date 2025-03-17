package org.flickit.assessment.data.jpa.kit.question;

import org.flickit.assessment.data.jpa.kit.question.advice.QuestionAdviceView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface QuestionJpaRepository extends JpaRepository<QuestionJpaEntity, QuestionJpaEntity.EntityId> {

    List<QuestionJpaEntity> findAllByKitVersionId(long kitVersionId);

    Optional<QuestionJpaEntity> findByIdAndKitVersionId(long id, long kitVersionId);

    Page<QuestionJpaEntity> findAllByQuestionnaireIdAndKitVersionIdOrderByIndex(Long questionnaireId, Long kitVersionId, Pageable pageable);

    boolean existsByIdAndKitVersionId(long id, long kitVersionId);

    void deleteByIdAndKitVersionId(long id, long kitVersionId);

    boolean existsByAnswerRangeIdAndKitVersionId(long answerRangeId, long kitVersionId);

    List<QuestionJpaEntity> findAllByIdInAndKitVersionIdAndQuestionnaireId(List<Long> ids, long kitVersionId, long questionnaireId);

    @Modifying
    @Query("""
            UPDATE QuestionJpaEntity q
            SET q.title = :title,
                q.code = :code,
                q.hint = :hint,
                q.index = :index,
                q.mayNotBeApplicable = :mayNotBeApplicable,
                q.advisable = :advisable,
                q.answerRangeId = :answerRangeId,
                q.lastModificationTime = :lastModificationTime,
                q.lastModifiedBy = :lastModifiedBy
            WHERE q.id = :id AND q.kitVersionId = :kitVersionId
        """)
    void update(@Param("id") Long id,
                @Param("kitVersionId") Long kitVersionId,
                @Param("title") String title,
                @Param("code") String code,
                @Param("index") Integer index,
                @Param("hint") String hint,
                @Param("mayNotBeApplicable") Boolean mayNotBeApplicable,
                @Param("advisable") Boolean advisable,
                @Param("answerRangeId") Long answerRangeId,
                @Param("lastModificationTime") LocalDateTime lastModificationTime,
                @Param("lastModifiedBy") UUID lastModifiedBy);

    @Query("""
            SELECT q as question,
                qi as questionImpact
            FROM QuestionJpaEntity q
            LEFT JOIN QuestionImpactJpaEntity qi ON q.id = qi.questionId AND qi.kitVersionId = q.kitVersionId
            WHERE q.kitVersionId = :kitVersionId
        """)
    List<QuestionJoinQuestionImpactView> loadByKitVersionId(@Param("kitVersionId") Long kitVersionId);

    @Query("""
            SELECT DISTINCT q
            FROM QuestionJpaEntity q
            LEFT JOIN QuestionImpactJpaEntity qi ON q.id = qi.questionId AND q.kitVersionId = qi.kitVersionId
            LEFT JOIN AttributeJpaEntity at ON qi.attributeId = at.id AND qi.kitVersionId = at.kitVersionId
            WHERE at.subjectId = :subjectId AND at.kitVersionId = :kitVersionId
        """)
    List<QuestionJpaEntity> findBySubjectId(@Param("subjectId") long subjectId, @Param("kitVersionId") Long kitVersionId);

    @Query("""
            SELECT COUNT (DISTINCT q.id)
            FROM QuestionJpaEntity q
            LEFT JOIN QuestionImpactJpaEntity qi ON q.id = qi.questionId AND q.kitVersionId = qi.kitVersionId
            LEFT JOIN AttributeJpaEntity at ON qi.attributeId = at.id AND qi.kitVersionId = at.kitVersionId
            WHERE at.subjectId = :subjectId AND at.kitVersionId = :kitVersionId
        """)
    Integer countDistinctBySubjectId(@Param("subjectId") long subjectId, @Param("kitVersionId") Long kitVersionId);

    @Query("""
            SELECT DISTINCT
                q.id AS questionId,
                qanso.id AS optionId,
                qanso.index AS optionIndex,
                qi.weight AS questionImpactWeight,
                qanso.value AS optionValue
            FROM QuestionJpaEntity q
            JOIN QuestionImpactJpaEntity qi ON q.id = qi.questionId AND q.kitVersionId = qi.kitVersionId
            JOIN AnswerOptionJpaEntity qanso ON q.answerRangeId = qanso.answerRangeId AND q.kitVersionId = qanso.kitVersionId
            WHERE
                q.advisable = TRUE
                AND q.kitVersionId = :kitVersionId
                AND qi.attributeId = :attributeId
                AND qi.maturityLevelId = :maturityLevelId
        """)
    List<ImpactfulQuestionView> findAdvisableImpactfulQuestions(@Param("kitVersionId") long kitVersionId,
                                                                @Param("attributeId") long attributeId,
                                                                @Param("maturityLevelId") long maturityLevelId);

    @Query("""
            SELECT DISTINCT
                q.id AS questionId,
                anso.index AS answeredOptionIndex
            FROM QuestionJpaEntity q
            LEFT JOIN AnswerJpaEntity ans ON ans.assessmentResult.id = :assessmentResultId AND q.id = ans.questionId
            LEFT JOIN AnswerOptionJpaEntity anso ON ans.answerOptionId = anso.id AND q.kitVersionId = anso.kitVersionId
            WHERE
                q.id IN :questionIds
                AND q.kitVersionId = :kitVersionId
                AND (ans.answerOptionId IS NULL
                    OR (anso.index != (
                        SELECT MAX(sq_ans.index) FROM AnswerOptionJpaEntity sq_ans
                        WHERE sq_ans.answerRangeId = anso.answerRangeId AND sq_ans.kitVersionId = :kitVersionId
                    )))
        """)
    List<QuestionIdWithAnsweredOptionIndexView> findImprovableQuestions(@Param("assessmentResultId") UUID assessmentResultId,
                                                                        @Param("kitVersionId") long kitVersionId,
                                                                        @Param("questionIds") Collection<Long> questionIds);

    @Query("""
            SELECT
                q.id AS id,
                q.title AS title,
                q.index AS index,
                ao AS option,
                atr AS attribute,
                qsnnr AS questionnaire
            FROM QuestionJpaEntity q
            JOIN AnswerOptionJpaEntity ao ON q.answerRangeId = ao.answerRangeId AND q.kitVersionId = ao.kitVersionId
            JOIN QuestionnaireJpaEntity qsnnr ON q.questionnaireId = qsnnr.id AND q.kitVersionId = qsnnr.kitVersionId
            JOIN QuestionImpactJpaEntity qi ON qi.questionId = q.id AND qi.kitVersionId = q.kitVersionId
            JOIN AttributeJpaEntity atr ON qi.attributeId = atr.id AND qi.kitVersionId = atr.kitVersionId
            WHERE q.id IN :ids AND q.kitVersionId = :kitVersionId
        """)
    List<QuestionAdviceView> findAdviceQuestionsDetail(@Param("ids") List<Long> ids, @Param("kitVersionId") long kitVersionId);

    @Query("""
            SELECT MIN(q.index) as index,
                qn.id as questionnaireId
            FROM QuestionJpaEntity q JOIN QuestionnaireJpaEntity qn ON q.questionnaireId = qn.id AND q.kitVersionId = qn.kitVersionId
            JOIN AssessmentResultJpaEntity ar ON ar.kitVersionId = qn.kitVersionId
            WHERE ar.id = :assessmentResultId AND q.id NOT IN (
                    SELECT fq.id
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
                    WHERE fans.assessmentResult.id = :assessmentResultId AND qn.kitVersionId = fans.assessmentResult.kitVersionId)
            GROUP BY qn.id, qn.kitVersionId
            ORDER BY qn.id
        """)
    List<FirstUnansweredQuestionView> findQuestionnairesFirstUnansweredQuestion(@Param("assessmentResultId") UUID assessmentResultId);

    @Query("""
            SELECT
                qr as questionnaire,
                qsn as question,
                qi as questionImpact,
                ao as answerOption
            FROM QuestionJpaEntity qsn
            LEFT JOIN AnswerOptionJpaEntity ao on qsn.answerRangeId = ao.answerRangeId AND qsn.kitVersionId = ao.kitVersionId
            LEFT JOIN QuestionnaireJpaEntity qr on qsn.questionnaireId = qr.id AND qsn.kitVersionId = qr.kitVersionId
            LEFT JOIN QuestionImpactJpaEntity qi on qsn.id = qi.questionId AND qsn.kitVersionId = qi.kitVersionId
            WHERE qi.attributeId = :attributeId
                AND qi.maturityLevelId = :maturityLevelId
                AND qi.kitVersionId = :kitVersionId
            ORDER BY qr.title asc, qsn.index asc
        """)
    List<AttributeLevelImpactfulQuestionsView> findByAttributeIdAndMaturityLevelIdAndKitVersionId(@Param("attributeId") long attributeId,
                                                                                                  @Param("maturityLevelId") long maturityLevelId,
                                                                                                  @Param("kitVersionId") long kitVersionId);

    @Query("""
            SELECT COUNT(q)
            FROM QuestionJpaEntity q
            WHERE q.kitVersionId = :kitVersionId
        """)
    int countByKitVersionId(@Param("kitVersionId") long kitVersionId);

    @Query("""
            SELECT
                qsn as question,
                qi as questionImpact
            FROM QuestionJpaEntity qsn
            LEFT JOIN QuestionImpactJpaEntity qi on qsn.id = qi.questionId AND qsn.kitVersionId = qi.kitVersionId
            WHERE qi.attributeId = :attributeId
                AND qi.kitVersionId = :kitVersionId
            ORDER BY qsn.questionnaireId asc, qsn.index asc
        """)
    List<AttributeImpactfulQuestionsView> findByAttributeIdAndKitVersionId(Long attributeId, Long kitVersionId);

    @Modifying
    @Query("""
            UPDATE QuestionJpaEntity q
            SET q.answerRangeId = :answerRangeId,
                q.lastModificationTime = :lastModificationTime,
                q.lastModifiedBy = :lastModifiedBy
            WHERE q.id = :id AND q.kitVersionId = :kitVersionId
        """)
    void updateAnswerRange(@Param("id") Long id,
                           @Param("kitVersionId") Long kitVersionId,
                           @Param("answerRangeId") Long answerRangeId,
                           @Param("lastModificationTime") LocalDateTime lastModificationTime,
                           @Param("lastModifiedBy") UUID lastModifiedBy);

    @Query("""
            SELECT q.index AS questionIndex, qn.id AS questionnaireId, qn.title AS questionnaireTitle
            FROM QuestionJpaEntity q
            JOIN QuestionnaireJpaEntity qn ON qn.id = q.questionnaireId AND qn.kitVersionId = q.kitVersionId
            WHERE q.kitVersionId = :kitVersionId AND q.answerRangeId IS NULL
        """)
    List<QuestionQuestionnaireView> findAllByKitVersionIdAndWithoutAnswerRange(long kitVersionId);

    @Query("""
            SELECT q.index as questionIndex, q.questionnaireId as questionnaireId, qr.title as questionnaireTitle
            FROM QuestionJpaEntity q
            JOIN QuestionnaireJpaEntity qr ON q.questionnaireId = qr.id AND qr.kitVersionId = q.kitVersionId
            LEFT JOIN QuestionImpactJpaEntity qi ON qi.questionId = q.id AND qi.kitVersionId = q.kitVersionId
            WHERE q.kitVersionId = :kitVersionId AND qi.id IS null
        """)
    List<QuestionQuestionnaireView> findAllByKitVersionIdAndWithoutImpact(@Param("kitVersionId") long kitVersionId);
}
