package org.flickit.assessment.users.application.port.out.spaceinvitee;

public interface DeleteSpaceInvitationPort {

    void deleteSpaceInvitation(long spaceId, String email);
}
