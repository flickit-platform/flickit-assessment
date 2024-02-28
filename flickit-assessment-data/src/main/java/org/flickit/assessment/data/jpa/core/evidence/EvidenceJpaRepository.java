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
    @Query("""
        UPDATE EvidenceJpaEntity e SET
            e.description = :description,
            e.type = :type,
            e.lastModificationTime = :lastModificationTime,
            e.lastModifiedBy = :lastModifiedBy
        WHERE e.id = :id
        """)
    void update(@Param(value = "id") UUID id,
                @Param(value = "description") String description,
                @Param(value = "type") Integer type,
                @Param(value = "lastModificationTime") LocalDateTime lastModificationTime,
                @Param(value = "lastModifiedBy") UUID lastModifiedBy);

    @Modifying
    @Query("""
        UPDATE EvidenceJpaEntity e SET
            e.deleted = true
        WHERE e.id = :id
        """)
    void delete(@Param(value = "id") UUID id);

    boolean existsByIdAndDeletedFalse(@Param(value = "id") UUID id);

    @Query(value = """
            SELECT evd.description AS description
            FROM AttributeJpaEntity atr
            JOIN QuestionImpactJpaEntity qi ON atr.id = qi.attributeId
            JOIN QuestionJpaEntity q ON qi.questionId = q.id
            JOIN EvidenceJpaEntity evd ON evd.questionId = q.id
            WHERE evd.assessmentId = :assessmentId
                AND evd.type = :type
                AND evd.deleted = false
                AND atr.id = :attributeId
            ORDER BY evd.lastModificationTime DESC
    """, countQuery = """
            SELECT count(evd.description)
            FROM AttributeJpaEntity atr
            JOIN QuestionImpactJpaEntity qi ON atr.id = qi.attributeId
            JOIN QuestionJpaEntity q ON qi.questionId = q.id
            JOIN EvidenceJpaEntity evd ON evd.questionId = q.id
            WHERE evd.assessmentId = :assessmentId
                AND evd.type = :type
                AND evd.deleted = false
                AND atr.id = :attributeId
    """)
    Page<AttributeEvidenceView> findAssessmentAttributeEvidencesByTypeOrderByLastModificationTimeDesc(UUID assessmentId,
                                                                                                      Long attributeId,
                                                                                                      Integer type,
                                                                                                      Pageable pageable);
}
