package org.flickit.assessment.data.jpa.users.spaceuseraccess;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SpaceUserAccessJpaRepository extends JpaRepository<SpaceUserAccessJpaEntity, Long> {

    boolean existsByUserIdAndSpaceId(UUID userId, Long spaceId);
}
