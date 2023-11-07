package org.flickit.assessment.data.jpa.assessmentresult;

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

}
