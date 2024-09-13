package org.flickit.assessment.data.jpa.advice.advicenarration;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AdviceNarrationJpaRepository extends JpaRepository<AdviceNarrationJpaEntity, UUID> {

    Optional<AdviceNarrationJpaEntity> findByAssessmentResultId(UUID assessmentResultId);

    @Modifying
    @Query("""
        UPDATE AdviceNarrationJpaEntity a SET
        a.aiNarration = :aiNarration,
        a.aiNarrationTime = :aiNarrationTime
        WHERE a.assessmentResultId = :assessmentResultId
        """)
    void updateAiNarration(@Param("assessmentResultId") UUID assessmentResultId,
                           @Param("aiNarration") String aiNarration,
                           @Param("aiNarrationTime") LocalDateTime aiNarrationTime);
}
