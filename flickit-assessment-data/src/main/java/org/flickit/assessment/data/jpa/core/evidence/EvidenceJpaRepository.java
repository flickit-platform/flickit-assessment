package org.flickit.assessment.data.jpa.core.evidence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.UUID;

public interface EvidenceJpaRepository extends JpaRepository<EvidenceJpaEntity, UUID> {

    Page<EvidenceJpaEntity> findByQuestionIdAndAssessmentIdAndDeletedFalseOrderByLastModificationTimeDesc(
        Long questionId, UUID assessmentId, Pageable pageable);

    @Modifying
    @Query("UPDATE EvidenceJpaEntity e SET " +
        "e.description = :description, " +
        "e.lastModificationTime = :lastModificationTime, " +
        "e.lastModifiedBy = :lastModifiedBy " +
        "WHERE e.id = :id")
    void update(@Param(value = "id") UUID id,
                @Param(value = "description") String description,
                @Param(value = "lastModificationTime") LocalDateTime lastModificationTime,
                @Param(value = "lastModifiedBy") UUID lastModifiedBy);

    @Modifying
    @Query("UPDATE EvidenceJpaEntity e SET " +
        "e.deleted = true " +
        "WHERE e.id = :id")
    void delete(@Param(value = "id") UUID id);

    boolean existsByIdAndDeletedFalse(@Param(value = "id") UUID id);

    @Query("""
            SELECT evd.description AS description, evd.type AS type
            FROM AttributeJpaEntity atr
            JOIN QuestionImpactJpaEntity  qi
            ON atr.id = qi.attributeId
            JOIN QuestionJpaEntity q
            ON qi.questionId = q.id
            JOIN EvidenceJpaEntity evd
            ON evd.questionId = q.id
            WHERE evd.assessmentId = :assessmentId
            AND evd.deleted = false
            AND atr.id = :attributeId
            ORDER BY evd.lastModificationTime DESC
    """)
    Page<AttributeEvidenceView> findByAttributeIdAndAssessmentIdAndDeletedFalseOrderByLastModificationTimeDesc(UUID assessmentId,
                                                                                                               Long attributeId,Pageable pageable);
}
