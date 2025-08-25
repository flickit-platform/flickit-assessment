package org.flickit.assessment.data.jpa.kit.attribute;

import org.flickit.assessment.data.jpa.core.attribute.AttributeMaturityLevelSubjectView;
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

public interface AttributeJpaRepository extends JpaRepository<AttributeJpaEntity, AttributeJpaEntity.EntityId> {

    List<AttributeJpaEntity> findAllBySubjectIdAndKitVersionId(long subjectId, long kitVersionId);

    List<AttributeJpaEntity> findAllBySubjectIdInAndKitVersionId(Collection<Long> subjectId, long kitVersionId);

    List<AttributeJpaEntity> findAllByIdInAndKitVersionId(Collection<Long> attributedIds, long kitVersionId);

    Optional<AttributeJpaEntity> findByIdAndKitVersionId(long id, long kitVersionId);

    List<AttributeJpaEntity> findAllByKitVersionId(long kitVersionId);

    boolean existsByIdAndKitVersionId(long id, long kitVersionId);

    void deleteByIdAndKitVersionId(long id, long kitVersionId);

    List<AttributeJpaEntity> findAllByIdInAndKitVersionIdAndSubjectId(List<Long> ids, long kitVersionId, long subjectId);

    int countByKitVersionId(long kitVersionId);

    @Modifying
    @Query("""
            UPDATE AttributeJpaEntity a
            SET a.code = :code,
                a.title = :title,
                a.index = :index,
                a.description = :description,
                a.weight = :weight,
                a.translations = :translations,
                a.lastModificationTime = :lastModificationTime,
                a.lastModifiedBy = :lastModifiedBy,
                a.subjectId = :subjectId
            WHERE a.id = :id AND a.kitVersionId = :kitVersionId
        """)
    void update(@Param("id") long id,
                @Param("kitVersionId") long kitVersionId,
                @Param("code") String code,
                @Param("title") String title,
                @Param("index") int index,
                @Param("description") String description,
                @Param("weight") int weight,
                @Param("translations") String translations,
                @Param("lastModificationTime") LocalDateTime lastModificationTime,
                @Param("lastModifiedBy") UUID lastModifiedBy,
                @Param("subjectId") long subjectId);

    @Query("""
            SELECT
                qr AS questionnaire,
                qsn AS question,
                ans AS answer,
                qi AS questionImpact,
                ao AS option,
                CASE
                    WHEN ans.isNotApplicable = true THEN 0.0
                    ELSE ROUND(COALESCE(ao.value, 0.0) * qi.weight, 2)
                END AS gainedScore,
                CASE
                    WHEN ans.isNotApplicable = true THEN 0.0
                    ELSE ROUND(qi.weight - COALESCE(ao.value, 0.0) * qi.weight, 2)
                END AS missedScore,
                COUNT(e.id) AS evidenceCount
            FROM QuestionJpaEntity qsn
            LEFT JOIN AnswerJpaEntity ans on ans.questionId = qsn.id and ans.assessmentResult.id = :assessmentResultId
            LEFT JOIN EvidenceJpaEntity e on ans.questionId = e.questionId and e.assessmentId = :assessmentId and e.deleted = false and e.type IS NOT NULL
            LEFT JOIN AnswerOptionJpaEntity ao on ans.answerOptionId = ao.id and ao.kitVersionId = :kitVersionId
            LEFT JOIN QuestionnaireJpaEntity qr on qsn.questionnaireId = qr.id and qsn.kitVersionId = qr.kitVersionId
            LEFT JOIN QuestionImpactJpaEntity qi on qsn.id = qi.questionId and qsn.kitVersionId = qi.kitVersionId
            WHERE qi.attributeId = :attributeId
                AND qi.maturityLevelId = :maturityLevelId
                AND qsn.kitVersionId = :kitVersionId
            GROUP BY
                qr, qsn, ans, qi, ao
        """)
    Page<ImpactFullQuestionsView> findImpactFullQuestionsScore(@Param("assessmentId") UUID assessmentId,
                                                               @Param("assessmentResultId") UUID assessmentResultId,
                                                               @Param("kitVersionId") long kitVersionId,
                                                               @Param("attributeId") Long attributeId,
                                                               @Param("maturityLevelId") Long maturityLevelId,
                                                               Pageable pageable);

    @Query("""
            SELECT COUNT(DISTINCT(q.id))
            FROM QuestionJpaEntity q
            JOIN QuestionImpactJpaEntity qi ON qi.questionId = q.id AND qi.kitVersionId = q.kitVersionId
            WHERE qi.attributeId = :attributeId
                AND qi.kitVersionId = :kitVersionId
        """)
    Integer countAttributeImpactfulQuestions(@Param("attributeId") long attributeId, @Param("kitVersionId") long kitVersionId);

    @Query("""
            SELECT at as attribute,
                s as subject
            FROM AttributeJpaEntity at
                JOIN SubjectJpaEntity s ON at.subjectId = s.id AND at.kitVersionId = s.kitVersionId
            WHERE at.kitVersionId = :kitVersionId
            ORDER BY s.index, at.index
        """)
    Page<AttributeJoinSubjectView> findAllByKitVersionId(long kitVersionId, Pageable pageable);

    @Query("""
            SELECT at
            FROM AttributeJpaEntity at
            LEFT JOIN QuestionImpactJpaEntity qi ON qi.attributeId = at.id AND qi.kitVersionId = at.kitVersionId
            WHERE at.kitVersionId = :kitVersionId AND qi.id IS NULL
        """)
    List<AttributeJpaEntity> findAllByKitVersionIdAndWithoutImpact(@Param("kitVersionId") long kitVersionId);

    @Query("""
            SELECT
                qsn.id as questionId,
                qi.weight as questionWeight,
                ans as answer,
                ao.value as optionValue,
                ans.isNotApplicable as isNotApplicable
            FROM QuestionJpaEntity qsn
            LEFT JOIN AnswerJpaEntity ans on ans.questionId = qsn.id and ans.assessmentResult.id = :assessmentResultId
            LEFT JOIN AnswerOptionJpaEntity ao on ans.answerOptionId = ao.id and ao.kitVersionId = :kitVersionId
            LEFT JOIN QuestionImpactJpaEntity qi on qsn.id = qi.questionId and qsn.kitVersionId = qi.kitVersionId
            WHERE qi.attributeId = :attributeId
                AND qi.maturityLevelId = :maturityLevelId
                AND qsn.kitVersionId = :kitVersionId
        """)
    List<AttributeQuestionView> findScoreStats(@Param("assessmentResultId") UUID assessmentResultId,
                                               @Param("kitVersionId") Long kitVersionId,
                                               @Param("attributeId") Long attributeId,
                                               @Param("maturityLevelId") Long maturityLevelId);

    @Query("""
            SELECT
                at AS attribute,
                av AS attributeValue,
                ml AS maturityLevel,
                su AS subject
            FROM AttributeJpaEntity at
            JOIN AttributeValueJpaEntity av ON at.id = av.attributeId AND av.assessmentResult.kitVersionId = at.kitVersionId
            JOIN MaturityLevelJpaEntity ml ON av.maturityLevelId = ml.id AND at.kitVersionId = ml.kitVersionId
            JOIN SubjectJpaEntity su ON su.id = at.subjectId AND su.kitVersionId = ml.kitVersionId
            WHERE av.assessmentResult.assessment.id = :assessmentId
        """)
    List<AttributeMaturityLevelSubjectView> findAllByAssessmentIdWithSubjectAndMaturityLevel(@Param("assessmentId") UUID assessmentId);

    @Query("""
            SELECT
                qsn AS question,
                ans AS answer,
                qi AS questionImpact,
                ao AS answerOption
            FROM QuestionJpaEntity qsn
            LEFT JOIN AnswerJpaEntity ans on ans.questionId = qsn.id and ans.assessmentResult.id = :assessmentResultId
            LEFT JOIN AnswerOptionJpaEntity ao on ans.answerOptionId = ao.id and ao.kitVersionId = :kitVersionId
            LEFT JOIN QuestionImpactJpaEntity qi on qsn.id = qi.questionId and qsn.kitVersionId = qi.kitVersionId
            WHERE qi.attributeId = :attributeId
                AND qsn.kitVersionId = :kitVersionId
                AND ans.isNotApplicable IS NOT TRUE
        """)
    List<QuestionAnswerView> findAttributeQuestionsAndAnswers(@Param("assessmentResultId") UUID assessmentResultId,
                                                              @Param("kitVersionId") Long kitVersionId,
                                                              @Param("attributeId") long attributeId);

    @Query("""
            SELECT a
            FROM AttributeJpaEntity a
            WHERE a.kitVersionId = :kitVersionId
              AND EXISTS (
                  SELECT 1
                  FROM QuestionImpactJpaEntity qi
                  WHERE qi.attributeId = a.id
                    AND qi.kitVersionId = a.kitVersionId
              )
              AND NOT EXISTS (
                  SELECT 1
                  FROM QuestionImpactJpaEntity qi
                  JOIN QuestionJpaEntity q ON q.id = qi.questionId AND q.kitVersionId = qi.kitVersionId
                  WHERE qi.attributeId = a.id
                    AND qi.kitVersionId = a.kitVersionId
                    AND q.measureId IS NOT NULL
              )
        """)
    List<AttributeJpaEntity> findAllByKitVersionIdAndWithoutMeasures(@Param("kitVersionId") Long kitVersionId);

    @Query("""
            SELECT
                qsn AS question,
                ans AS answer,
                qi AS questionImpact,
                ao AS answerOption
            FROM QuestionJpaEntity qsn
            LEFT JOIN AnswerJpaEntity ans on ans.questionId = qsn.id and ans.assessmentResult.id = :assessmentResultId
            LEFT JOIN AnswerOptionJpaEntity ao on ans.answerOptionId = ao.id and ao.kitVersionId = :kitVersionId
            LEFT JOIN QuestionImpactJpaEntity qi on qsn.id = qi.questionId and qsn.kitVersionId = qi.kitVersionId
            WHERE qi.attributeId = :attributeId
                AND qsn.kitVersionId = :kitVersionId
                AND ans.isNotApplicable IS NOT TRUE
                AND qsn.measureId = :measureId
        """)
    List<QuestionAnswerView> findAttributeMeasureQuestionsAndAnswers(@Param("assessmentResultId") UUID assessmentResultId,
                                                                     @Param("kitVersionId") Long kitVersionId,
                                                                     @Param("attributeId") long attributeId,
                                                                     @Param("measureId") long measureId);
}
