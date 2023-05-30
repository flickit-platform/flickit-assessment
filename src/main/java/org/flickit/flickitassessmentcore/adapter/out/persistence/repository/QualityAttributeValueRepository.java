package org.flickit.flickitassessmentcore.adapter.out.persistence.repository;

import org.flickit.flickitassessmentcore.adapter.out.persistence.entity.QualityAttributeValueEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface QualityAttributeValueRepository extends JpaRepository<QualityAttributeValueEntity, UUID> {
}
