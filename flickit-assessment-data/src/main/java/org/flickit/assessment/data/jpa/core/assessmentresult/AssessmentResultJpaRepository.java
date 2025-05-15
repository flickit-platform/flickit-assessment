package org.flickit.assessment.data.jpa.core.assessmentresult;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface AssessmentResultJpaRepository extends JpaRepository<AssessmentResultJpaEntity, UUID> {

    Optional<AssessmentResultJpaEntity> findFirstByAssessment_IdOrderByLastModificationTimeDesc(UUID assessmentId);

    @Modifying
    @Query("""
            UPDATE AssessmentResultJpaEntity a
            SET a.isCalculateValid = FALSE
            WHERE a.id = :id
        """)
    void invalidateCalculateById(@Param(value = "id") UUID id);

    @Modifying
    @Query("""
            UPDATE AssessmentResultJpaEntity a SET
            a.isConfidenceValid = FALSE
            WHERE a.id = :id
        """)
    void invalidateConfidenceById(@Param(value = "id") UUID id);

    @Modifying
    @Query("""
            UPDATE AssessmentResultJpaEntity a
            SET a.maturityLevelId = :maturityLevelId,
                a.isCalculateValid = :isCalculateValid,
                a.lastModificationTime = :lastModificationTime,
                a.lastCalculationTime = :lastCalculationTime
            WHERE a.id = :id
        """)
    void updateAfterCalculate(@Param(value = "id") UUID id,
                              @Param(value = "maturityLevelId") Long maturityLevelId,
                              @Param(value = "isCalculateValid") boolean isCalculateValid,
                              @Param(value = "lastModificationTime") LocalDateTime lastModificationTime,
                              @Param(value = "lastCalculationTime") LocalDateTime lastCalculationTime);

    @Modifying
    @Query("""
            UPDATE AssessmentResultJpaEntity a
            SET a.confidenceValue = :confidenceValue,
                a.isConfidenceValid = :isConfidenceValid,
                a.lastModificationTime = :lastModificationTime,
                a.lastConfidenceCalculationTime = :lastConfidenceCalculationTime
            WHERE a.id = :id
        """)
    void updateAfterCalculateConfidence(@Param(value = "id") UUID id,
                                        @Param(value = "confidenceValue") Double confidenceValue,
                                        @Param(value = "isConfidenceValid") boolean isConfidenceValid,
                                        @Param(value = "lastModificationTime") LocalDateTime lastModificationTime,
                                        @Param(value = "lastConfidenceCalculationTime") LocalDateTime lastConfidenceCalculationTime);

    @Modifying
    @Query("""
            UPDATE AssessmentResultJpaEntity a
            SET a.kitVersionId = :kitVersionId
            WHERE a.id = :id
        """)
    void updateKitVersionId(@Param("id") UUID id, @Param("kitVersionId") long kitVersionId);
}
