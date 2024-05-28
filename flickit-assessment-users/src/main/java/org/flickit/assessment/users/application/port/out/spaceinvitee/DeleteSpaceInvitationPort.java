package org.flickit.assessment.users.application.port.out.spaceinvitee;

import java.util.UUID;

public interface DeleteSpaceInvitationPort {

    void deleteSpaceInvitation(UUID inviteId);
}
