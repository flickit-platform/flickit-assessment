package org.flickit.flickitassessmentcore.adapter.out.persistence.attributematurityscore;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AttributeMaturityScoreJpaRepository extends JpaRepository<AttributeMaturityScoreJpaEntity, UUID> {

    Optional<AttributeMaturityScoreJpaEntity> findByAttributeValue_IdAndMaturityLevelId(UUID id, long maturityLevelId);
}
