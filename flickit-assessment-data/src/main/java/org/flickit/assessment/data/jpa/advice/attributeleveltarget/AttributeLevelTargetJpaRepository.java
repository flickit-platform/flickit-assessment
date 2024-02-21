package org.flickit.assessment.data.jpa.advice.attributeleveltarget;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AttributeLevelTargetJpaRepository extends JpaRepository<AttributeLevelTargetJpaEntity, UUID> {
}
