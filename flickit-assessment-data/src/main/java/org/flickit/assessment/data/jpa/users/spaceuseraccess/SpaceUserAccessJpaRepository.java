package org.flickit.assessment.data.jpa.users.spaceuseraccess;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface SpaceUserAccessJpaRepository extends JpaRepository<SpaceUserAccessJpaEntity, Long> {

    @Query("SELECT s.ownerId FROM SpaceJpaEntity as s where s.id = :id") //TODO: add this:  and deleted=false
    Optional<UUID> loadOwnerIdById(Long id);

    boolean existsByUserIdAndSpaceId(UUID userId, Long spaceId);
}
