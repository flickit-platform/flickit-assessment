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

    boolean existsByAssessmentResultId(UUID assessmentResultId);

    Optional<AssessmentInsightJpaEntity> findByAssessmentResultId(UUID assessmentResultId);

    @Modifying
    @Query("""
            UPDATE AssessmentInsightJpaEntity a
            SET a.insight = :insight,
                a.insightTime = :insightTime,
                a.insightBy = :insightBy
            WHERE a.id = :id
        """)
    void update(@Param("id") UUID id, @Param("insight") String insight, @Param("insightTime") LocalDateTime insightTime, @Param("insightBy") UUID insightBy);
}
