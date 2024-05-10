package org.flickit.assessment.data.jpa.users.space;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface SpaceJpaRepository extends JpaRepository<SpaceJpaEntity, Long> {

    @Query("""
        SELECT s.ownerId
        FROM SpaceJpaEntity as s
        where s.id = :id
        """) //TODO: add this:  and deleted=false
    Optional<UUID> loadOwnerIdById(Long id);
}
