package org.flickit.assessment.data.jpa.core.assessmentresult;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface AssessmentResultJpaRepository extends JpaRepository<AssessmentResultJpaEntity, UUID> {

    @Modifying
    @Query("UPDATE AssessmentResultJpaEntity a SET " +
        "a.isCalculateValid = :isCalculateValid, " +
        "a.isConfidenceValid = :isConfidenceValid " +
        "WHERE a.id = :id")
    void invalidateById(@Param(value = "id") UUID id,
                        @Param(value = "isCalculateValid")Boolean isCalculateValid,
                        @Param(value = "isConfidenceValid")Boolean isConfidenceValid);

    Optional<AssessmentResultJpaEntity> findFirstByAssessment_IdOrderByLastModificationTimeDesc(UUID assessmentId);

    @Modifying
    @Query("UPDATE AssessmentResultJpaEntity a SET " +
        "a.maturityLevelId = :maturityLevelId, " +
        "a.isCalculateValid = :isCalculateValid, " +
        "a.lastModificationTime = :lastModificationTime " +
        "WHERE a.id = :id")
    void updateAfterCalculate(@Param(value = "id") UUID id,
                              @Param(value = "maturityLevelId") Long maturityLevelId,
                              @Param(value = "isCalculateValid") boolean isCalculateValid,
                              @Param(value = "lastModificationTime") LocalDateTime lastModificationTime);

    @Modifying
    @Query("UPDATE AssessmentResultJpaEntity a SET " +
        "a.confidenceValue = :confidenceValue, " +
        "a.isConfidenceValid = :isConfidenceValid, " +
        "a.lastModificationTime = :lastModificationTime " +
        "WHERE a.id = :id")
    void updateAfterCalculateConfidence(@Param(value = "id") UUID id,
                                        @Param(value = "confidenceValue") Double confidenceValue,
                                        @Param(value = "isConfidenceValid") boolean isConfidenceValid,
                                        @Param(value = "lastModificationTime") LocalDateTime lastModificationTime);

    @Modifying
    @Query("UPDATE AssessmentResultJpaEntity a SET " +
        "a.isCalculateValid = false " +
        "WHERE a.assessment.id IN (SELECT b.id FROM AssessmentJpaEntity b WHERE b.assessmentKitId = :kitId)")
    void invalidateByKitId(@Param(value = "kitId") Long kitId);


}
