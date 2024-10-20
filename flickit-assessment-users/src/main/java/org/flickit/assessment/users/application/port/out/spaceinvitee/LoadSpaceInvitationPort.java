package org.flickit.assessment.users.application.port.out.spaceinvitee;

import org.flickit.assessment.users.application.domain.SpaceInvitee;

import java.util.Optional;
import java.util.UUID;

public interface LoadSpaceInvitationPort {

    Optional<SpaceInvitee> loadSpaceInvitation(UUID inviteId);
}
