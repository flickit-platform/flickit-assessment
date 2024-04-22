package org.flickit.assessment.users.application.port.in.spaceinvitee;

import java.util.List;
import java.util.UUID;

public interface LoadSpaceUserInvitationsPort {

    List<Invitation> loadInvitations(String email);

    record Invitation(long spaceId, UUID createdBy){}
}
