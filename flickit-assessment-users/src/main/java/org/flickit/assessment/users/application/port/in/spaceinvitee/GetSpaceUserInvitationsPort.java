package org.flickit.assessment.users.application.port.in.spaceinvitee;

import org.flickit.assessment.users.application.domain.SpaceInvitation;

import java.util.List;

public interface GetSpaceUserInvitationsPort {

    List<SpaceInvitation> loadInvitations(String email);
}
