package org.flickit.flickitassessmentcore.adapter.out.persistence.assessmentresult;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface AssessmentResultJpaRepository extends JpaRepository<AssessmentResultJpaEntity, UUID> {

    @Modifying
    @Query("UPDATE AssessmentResultJpaEntity a SET a.isValid = false WHERE a.id = :id")
    void invalidateById(@Param(value = "id") UUID id);

    AssessmentResultJpaEntity findFirstByAssessment_IdOrderByLastModificationTimeDesc(UUID assessmentId);

    @Modifying
    @Query("update AssessmentResultJpaEntity a set a.maturityLevelId = :maturityLevelId, isValid = :isValid where a.id = :id")
    void updateMaturityLeveAndIsValidById(@Param(value = "id") UUID id,
                                          @Param(value = "maturityLevelId") Long maturityLevelId,
                                          @Param(value = "isValid") boolean isValid);
}
