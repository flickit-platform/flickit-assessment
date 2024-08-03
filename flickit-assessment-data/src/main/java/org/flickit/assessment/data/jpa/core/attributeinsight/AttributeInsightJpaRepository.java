package org.flickit.assessment.data.jpa.core.attributeinsight;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AttributeInsightJpaRepository extends JpaRepository<AttributeInsightJpaEntity, AttributeInsightJpaEntity.EntityId> {

    Optional<AttributeInsightJpaEntity> findByAssessmentResultIdAndAttributeId(UUID assessmentResultId, Long attributeId);
}
