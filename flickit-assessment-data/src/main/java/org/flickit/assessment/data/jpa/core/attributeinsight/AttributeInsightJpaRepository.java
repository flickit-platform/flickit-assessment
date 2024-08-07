package org.flickit.assessment.data.jpa.core.attributeinsight;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface AttributeInsightJpaRepository extends JpaRepository<AttributeInsightJpaEntity, AttributeInsightJpaEntity.EntityId> {

    Optional<AttributeInsightJpaEntity> findByAssessmentResultIdAndAttributeId(UUID assessmentResultId, Long attributeId);

    @Modifying
    @Query("""
            UPDATE AttributeInsightJpaEntity a
            SET a.aiInsight = :aiInsight,
                a.aiInsightTime = :aiInsightTime
            WHERE a.assessmentResultId = :assessmentResultId AND a.attributeId = :attributeId
        """)
    void updateAiInsight(@Param("assessmentResultId") UUID assessmentResultId,
                         @Param("attributeId") Long attributeId,
                         @Param("aiInsight") String aiInsight,
                         @Param("aiInsightTime") LocalDateTime aiInsightTime);
}
