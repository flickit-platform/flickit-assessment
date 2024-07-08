package org.flickit.assessment.data.jpa.core.evidence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface EvidenceJpaRepository extends JpaRepository<EvidenceJpaEntity, UUID> {

    Page<EvidenceJpaEntity> findByQuestionIdAndAssessmentIdAndDeletedFalseOrderByLastModificationTimeDesc(Long questionId,
                                                                                                          UUID assessmentId,
                                                                                                          Pageable pageable);

    Optional<EvidenceJpaEntity> findByIdAndDeletedFalse(UUID id);

    @Modifying
    @Query("""
            UPDATE EvidenceJpaEntity e
            SET e.description = :description,
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
            UPDATE EvidenceJpaEntity e
            SET e.deleted = true
            WHERE e.id = :id
        """)
    void delete(@Param(value = "id") UUID id);

    @Query("""
            SELECT evd.description
            FROM QuestionJpaEntity q
            LEFT JOIN EvidenceJpaEntity evd ON q.id = evd.questionId
            WHERE evd.assessmentId = :assessmentId
                AND evd.type = :type
                AND evd.deleted = false
                AND q.id IN (SELECT qs.id
                             FROM QuestionJpaEntity qs
                             LEFT JOIN AssessmentResultJpaEntity ar ON qs.kitVersionId = ar.kitVersionId
                             LEFT JOIN QuestionImpactJpaEntity qi ON qs.id = qi.questionId
                             WHERE qi.attributeId = :attributeId AND ar.assessment.id = :assessmentId)
            ORDER BY evd.lastModificationTime DESC
        """)
    Page<String> findAssessmentAttributeEvidencesByTypeOrderByLastModificationTimeDesc(@Param(value = "assessmentId") UUID assessmentId,
                                                                                       @Param(value = "attributeId") Long attributeId,
                                                                                       @Param(value = "type") Integer type,
                                                                                       Pageable pageable);

    boolean existsByIdAndDeletedFalse(UUID evidenceId);
}
