package org.flickit.assessment.data.jpa.attributevalue;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface QualityAttributeValueJpaRepository extends JpaRepository<QualityAttributeValueJpaEntity, UUID> {

    List<QualityAttributeValueJpaEntity> findByAssessmentResultId(UUID resultId);

    @Modifying
    @Query("update QualityAttributeValueJpaEntity a set a.maturityLevelId = :maturityLevelId where a.id = :id")
    void updateMaturityLevelById(@Param(value = "id") UUID id,
                                 @Param(value = "maturityLevelId") Long maturityLevelId);

    @Modifying
    @Query("update QualityAttributeValueJpaEntity a set a.confidenceLevelValue = :confidenceLevelValue where a.id = :id")
    void updateConfidenceLevelById(@Param(value = "id") UUID id,
                                 @Param(value = "confidenceLevelValue") Double confidenceLevelValue);

}
