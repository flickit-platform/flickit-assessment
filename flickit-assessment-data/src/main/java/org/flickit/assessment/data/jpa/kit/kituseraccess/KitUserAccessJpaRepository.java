package org.flickit.assessment.data.jpa.kit.kituseraccess;

import org.springframework.data.jpa.repository.JpaRepository;

public interface KitUserAccessJpaRepository extends JpaRepository<KitUserAccessJpaEntity, KitUserAccessJpaEntity.KitUserAccessKey> {
}
