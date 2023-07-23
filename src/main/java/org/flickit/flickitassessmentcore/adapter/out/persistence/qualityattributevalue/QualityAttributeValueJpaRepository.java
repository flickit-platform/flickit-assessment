package org.flickit.flickitassessmentcore.adapter.out.persistence.qualityattributevalue;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface QualityAttributeValueJpaRepository extends JpaRepository<QualityAttributeValueJpaEntity, UUID> {

}
