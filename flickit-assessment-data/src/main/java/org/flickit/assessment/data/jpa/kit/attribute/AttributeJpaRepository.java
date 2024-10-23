package org.flickit.assessment.data.jpa.kit.attribute;

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

    boolean existsByIdAndKitVersionId(long id, long kitVersionId);

    void deleteByIdAndKitVersionId(long id, long kitVersionId);

    List<AttributeJpaEntity> findAllByIdInAndKitVersionIdAndSubjectId(List<Long> ids, long kitVersionId, long subjectId);

    @Modifying
    @Query("""
            UPDATE AttributeJpaEntity a
            SET a.code = :code,
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
                @Param("code") String code,
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
            LEFT JOIN AnswerOptionImpactJpaEntity ov on ov.questionImpactId = qi.id and ov.optionId = ans.answerOptionId AND ov.kitVersionId = qi.kitVersionId
                AND ov.kitVersionId = qi.kitVersionId
            WHERE qi.attributeId = :attributeId
                AND qi.maturityLevelId = :maturityLevelId
                AND qsn.kitVersionId = :kitVersionId
            ORDER BY qr.title asc, qsn.index asc
        """)
    List<ImpactFullQuestionsView> findImpactFullQuestionsScore(@Param("assessmentResultId") UUID assessmentResultId,
                                                               @Param("kitVersionId") long kitVersionId,
                                                               @Param("attributeId") Long attributeId,
                                                               @Param("maturityLevelId") Long maturityLevelId);

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
}
