package org.flickit.assessment.data.jpa.users.spaceinvitee;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SpaceInviteeJpaRepository extends JpaRepository<SpaceInviteeJpaEntity, UUID> {

    void deleteByEmail(String email);

    List<SpaceInviteeJpaEntity> findByEmail(String email);
}
