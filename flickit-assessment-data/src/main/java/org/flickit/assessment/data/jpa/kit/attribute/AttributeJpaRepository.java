package org.flickit.assessment.data.jpa.kit.attribute;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.*;

public interface AttributeJpaRepository extends JpaRepository<AttributeJpaEntity, AttributeJpaEntity.EntityId> {

    List<AttributeJpaEntity> findAllBySubjectId(long subjectId);
    List<AttributeJpaEntity> findAllBySubjectIdIn(Collection<Long> subjectId);
    List<AttributeJpaEntity> findAllByIdInAndKitVersionId(Collection<Long> id, Long kitVersionId);
    List<AttributeJpaEntity> findAllBySubjectIdInAndKitVersionId(Collection<Long> subjectIds, long kitVersionId);

    @Modifying
    @Query("""
            UPDATE AttributeJpaEntity a SET
                a.title = :title,
                a.index = :index,
                a.description = :description,
                a.weight = :weight,
                a.lastModificationTime = :lastModificationTime,
                a.lastModifiedBy = :lastModifiedBy,
                a.subjectId = :subjectId
            WHERE a.id = :id AND a.kitVersionId = :kitVersionId
        """)
    void update(@Param("id") long id,
                @Param("kitVersionId") long kitVersionId,
                @Param("title") String title,
                @Param("index") int index,
                @Param("description") String description,
                @Param("weight") int weight,
                @Param("lastModificationTime") LocalDateTime lastModificationTime,
                @Param("lastModifiedBy") UUID lastModifiedBy,
                @Param("subjectId") long subjectId);

    @Query("""
            SELECT
              qr.title as questionnaireTitle,
              qsn.id as questionId,
              qsn.index as questionIndex,
              qsn.title as questionTitle,
              ans as answer,
              qi as questionImpact,
              ov as optionImpact,
              ao.index as optionIndex,
              ao.title as optionTitle
            FROM QuestionJpaEntity qsn
            LEFT JOIN AnswerJpaEntity ans on ans.questionId = qsn.id and ans.assessmentResult.id = :assessmentResultId
            LEFT JOIN AnswerOptionJpaEntity ao on ans.answerOptionId = ao.id and ao.kitVersionId = :kitVersionId
            LEFT JOIN QuestionnaireJpaEntity qr on qsn.questionnaireId = qr.id and qsn.kitVersionId = qr.kitVersionId
            LEFT JOIN QuestionImpactJpaEntity qi on qsn.id = qi.questionId and qsn.kitVersionId = qi.kitVersionId
            LEFT JOIN AnswerOptionImpactJpaEntity ov on ov.questionImpact.id = qi.id and ov.optionId = ans.answerOptionId
            and ov.kitVersionId = qi.kitVersionId
            WHERE
              qi.attributeId = :attributeId
              AND qi.maturityLevel.id = :maturityLevelId
              and qsn.kitVersionId = :kitVersionId
            ORDER BY qr.title asc, qsn.index asc
        """)
    List<ImpactFullQuestionsView> findImpactFullQuestionsScore(@Param("assessmentResultId") UUID assessmentResultId,
                                                               @Param("kitVersionId") long kitVersionId,
                                                               @Param("attributeId") Long attributeId,
                                                               @Param("maturityLevelId") Long maturityLevelId);

    List<AttributeJpaEntity> findAllByKitVersionIdAndRefNumIn(Long kitVersionId, List<UUID> refNums);

    List<AttributeJpaEntity> findByIdIn(@Param(value = "ids") List<Long> ids);

    Optional<AttributeJpaEntity> findByIdAndKitVersionId(long id, long kitVersionId);

    @Query("""
            SELECT COUNT(DISTINCT(q.id, q.kitVersionId)) FROM QuestionJpaEntity q
            JOIN QuestionImpactJpaEntity qi ON qi.questionId = q.id AND qi.kitVersionId = q.kitVersionId
            WHERE qi.attributeId = :attributeId AND qi.kitVersionId = :kitVersionId
        """)
    Integer countAttributeImpactfulQuestions(@Param("attributeId") long attributeId, @Param("kitVersionId") long kitVersionId);

    @Query("""
              SELECT COUNT(a) > 0
              FROM AttributeJpaEntity a
              LEFT JOIN KitVersionJpaEntity kv ON a.kitVersionId = kv.id
              WHERE  a.id = :id AND kv.kit.id = :kitId
        """)
    boolean existsByIdAndKitId(@Param("id") long id, @Param("kitId") long kitId);
}
