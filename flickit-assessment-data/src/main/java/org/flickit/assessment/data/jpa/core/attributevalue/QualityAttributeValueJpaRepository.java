package org.flickit.assessment.data.jpa.core.attributevalue;

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
    @Query("update QualityAttributeValueJpaEntity a set a.confidenceValue = :confidenceValue where a.id = :id")
    void updateConfidenceValueById(@Param(value = "id") UUID id,
                                   @Param(value = "confidenceValue") Double confidenceValue);

}
