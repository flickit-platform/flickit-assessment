package org.flickit.assessment.data.jpa.core.assessmentinsight;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AssessmentInsightJpaRepository extends JpaRepository<AssessmentInsightJpaEntity, UUID> {

    Optional<AssessmentInsightJpaEntity> findByAssessmentResultId(UUID assessmentResultId);

    boolean existsByAssessmentResultId(UUID assessmentResultId);

    @Modifying
    @Query("""
            UPDATE AssessmentInsightJpaEntity a
            SET a.insight = :insight,
                a.insightTime = :insightTime,
                a.lastModificationTime = :lastModificationTime,
                a.insightBy = :insightBy,
                a.approved = :approved
            WHERE a.id = :id
        """)
    void update(@Param("id") UUID id,
                @Param("insight") String insight,
                @Param("insightTime") LocalDateTime insightTime,
                @Param("lastModificationTime") LocalDateTime lastModificationTime,
                @Param("insightBy") UUID insightBy,
                @Param("approved") boolean approved);

    @Modifying
    @Query("""
            UPDATE AssessmentInsightJpaEntity a
            SET a.approved = true,
                a.lastModificationTime = :lastModificationTime
            WHERE a.assessmentResultId = :assessmentResultId
        """)
    void approve(@Param("assessmentResultId") UUID assessmentResultId,
                 @Param("lastModificationTime") LocalDateTime lastModificationTime);
}
