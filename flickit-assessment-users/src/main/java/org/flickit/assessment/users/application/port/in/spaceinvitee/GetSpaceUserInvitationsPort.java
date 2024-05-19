package org.flickit.assessment.users.application.port.in.spaceinvitee;

import org.flickit.assessment.users.application.domain.SpaceInvitee;

import java.util.List;

public interface GetSpaceUserInvitationsPort {

    List<SpaceInvitee> loadInvitations(String email);
}
