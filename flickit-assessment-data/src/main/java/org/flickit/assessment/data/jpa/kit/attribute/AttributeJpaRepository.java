package org.flickit.assessment.data.jpa.kit.attribute;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface AttributeJpaRepository extends JpaRepository<AttributeJpaEntity, Long> {

    List<AttributeJpaEntity> findAllBySubjectId(long subjectId);

    @Modifying
    @Query("""
            UPDATE AttributeJpaEntity a SET
                a.title = :title,
                a.index = :index,
                a.description = :description,
                a.weight = :weight,
                a.lastModificationTime = :lastModificationTime,
                a.lastModifiedBy = :lastModifiedBy,
                a.subject.id = :subjectId
            WHERE a.id = :id
        """)
    void update(@Param("id") long id,
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
            LEFT JOIN AnswerOptionJpaEntity ao on ans.answerOptionId = ao.id
            LEFT JOIN QuestionnaireJpaEntity qr on qsn.questionnaireId = qr.id
            LEFT JOIN QuestionImpactJpaEntity qi on qsn.id = qi.questionId
            LEFT JOIN AnswerOptionImpactJpaEntity ov on ov.questionImpact.id = qi.id and ov.optionId = ans.answerOptionId
            WHERE
              qi.attributeId = :attributeId
              AND qi.maturityLevel.id = :maturityLevelId
            ORDER BY qr.title asc, qsn.index asc
        """)
    List<ImpactFullQuestionsView> findImpactFullQuestionsScore(@Param("assessmentResultId") UUID assessmentResultId,
                                                               @Param("attributeId") Long attributeId,
                                                               @Param("maturityLevelId") Long maturityLevelId);

    @Query("""
        SELECT a.referenceNumber
        FROM AttributeJpaEntity a
        WHERE a.id = :attributeId
        """)
    UUID findReferenceNumberById(@Param("attributeId") Long attributeId);

    AttributeJpaEntity findByKitVersionIdAndReferenceNumber(Long kitVersionId, UUID referenceNumber);

    List<AttributeJpaEntity> findAllByKitVersionIdAndReferenceNumberIn(Long kitVersionId, List<UUID> referenceNumbers);

    @Query("""
        SELECT a
        FROM AttributeJpaEntity a
        LEFT JOIN AssessmentKitJpaEntity k ON k.kitVersionId = a.kitVersionId
        LEFT JOIN AssessmentJpaEntity i ON i.id = :assessmentId AND i.assessmentKitId = k.id
        WHERE a.id IN :ids
        """)
    List<AttributeJpaEntity> findAllByIdsAndAssessmentId(List<Long> ids, UUID assessmentId);
}
