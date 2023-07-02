package org.flickit.flickitassessmentcore.adapter.out.persistence.qualityattributevalue;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface QualityAttributeValueJpaRepository extends JpaRepository<QualityAttributeValueJpaEntity, UUID> {

    @Query("select a from QualityAttributeValueJpaEntity a " +
        "where a.qualityAttributeId in :qaIds")
    List<QualityAttributeValueJpaEntity> findQualityAttributeValuesByQualityAttributeIds(@Param("qaIds") Set<Long> qaIds);
}
