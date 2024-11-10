package org.flickit.assessment.data.jpa.core.attributematurityscore;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AttributeMaturityScoreJpaRepository extends
    JpaRepository<AttributeMaturityScoreJpaEntity, AttributeMaturityScoreJpaEntity.EntityId> {

    Optional<AttributeMaturityScoreJpaEntity> findByAttributeValueIdAndMaturityLevelId(UUID attributeValueId, long maturityLevelId);

    List<AttributeMaturityScoreJpaEntity> findByAttributeValueIdIn(Collection<UUID> attributeValueIds);

    void deleteAllByAttributeValueIdIn(Collection<UUID> attributeValueIds);
}
