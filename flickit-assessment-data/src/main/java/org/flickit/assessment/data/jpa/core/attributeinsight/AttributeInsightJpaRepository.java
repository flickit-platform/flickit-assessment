package org.flickit.assessment.data.jpa.core.attributeinsight;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AttributeInsightJpaRepository extends
    JpaRepository<AttributeInsightJpaEntity, AttributeInsightJpaEntity.EntityId> {
}
