package org.flickit.assessment.data.jpa.kit.measure;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MeasureJpaRepository extends JpaRepository<MeasureJpaEntity, MeasureJpaEntity.EntityId> {
}
