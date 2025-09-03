package org.flickit.assessment.data.jpa.advice.adviceitem;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface AdviceItemJpaRepository extends JpaRepository<AdviceItemJpaEntity, UUID> {

    Page<AdviceItemJpaEntity> findByAssessmentResultId(UUID assessmentResultId, Pageable pageable);

    int countByAssessmentResultId(UUID assessmentResultId);

    void deleteByAssessmentResultIdAndCreatedByIsNullAndLastModifiedByIsNull(UUID assessmentResultId);

    boolean existsByAssessmentResultId(UUID assessmentResultId);

    @Query("""
            SELECT a.assessment.id
            FROM AdviceItemJpaEntity ad
            JOIN AssessmentResultJpaEntity a ON a.id = ad.assessmentResultId
            WHERE ad.id = :id
        """)
    Optional<UUID> findAssessmentIdById(UUID id);

    @Modifying
    @Query("""
            UPDATE AdviceItemJpaEntity a
            SET a.title = :title,
                a.description = :description,
                a.cost = :cost,
                a.priority = :priority,
                a.impact = :impact,
                a.lastModificationTime = :lastModificationTime,
                a.lastModifiedBy = :lastModifiedBy
            WHERE a.id = :id
        """)
    void update(UUID id,
                @Param("title") String title,
                @Param("description") String description,
                @Param("cost") int cost,
                @Param("priority") int priority,
                @Param("impact") int impact,
                @Param("lastModificationTime") LocalDateTime lastModificationTime,
                @Param("lastModifiedBy") UUID lastModifiedBy);
}
