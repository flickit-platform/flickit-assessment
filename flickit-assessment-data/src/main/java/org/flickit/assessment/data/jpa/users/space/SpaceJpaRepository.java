package org.flickit.assessment.data.jpa.users.space;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface SpaceJpaRepository extends JpaRepository<SpaceJpaEntity, Long> {

    @Query("""
            SELECT e.ownerId
            FROM SpaceJpaEntity as e
            WHERE e.id = :id
    """) // TODO: Add this after adding deleted field : and deleted=false
    Optional<UUID> loadOwnerIdById(long id);
}
