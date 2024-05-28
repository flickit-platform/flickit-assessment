package org.flickit.assessment.users.application.port.out.spaceinvitee;

import org.flickit.assessment.users.application.domain.SpaceInvitee;

import java.util.UUID;

public interface LoadSpaceInvitationPort {

    SpaceInvitee loadSpaceInvitation(UUID inviteId);
}
