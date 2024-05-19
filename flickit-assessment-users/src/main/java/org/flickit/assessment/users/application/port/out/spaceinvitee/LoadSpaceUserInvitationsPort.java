package org.flickit.assessment.users.application.port.out.spaceinvitee;

import org.flickit.assessment.users.application.domain.SpaceInvitee;

import java.util.List;

public interface LoadSpaceUserInvitationsPort {

    List<SpaceInvitee> loadInvitations(String email);
}
