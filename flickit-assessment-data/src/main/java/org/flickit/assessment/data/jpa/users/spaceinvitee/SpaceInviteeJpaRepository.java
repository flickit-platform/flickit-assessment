package org.flickit.assessment.data.jpa.users.spaceinvitee;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SpaceInviteeJpaRepository extends JpaRepository<SpaceInviteeJpaEntity, UUID> {

    void deleteByEmail(String email);

    Optional<SpaceInviteeJpaEntity> findByEmail(String email);
}
