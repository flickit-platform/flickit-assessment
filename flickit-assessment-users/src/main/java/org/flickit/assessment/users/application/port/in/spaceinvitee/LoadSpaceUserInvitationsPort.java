package org.flickit.assessment.users.application.port.in.spaceinvitee;

import java.util.List;
import java.util.UUID;

public interface LoadSpaceUserInvitationsPort {

    List<Result> loadInvitations(String email);

    record Result(long spaceId, UUID createdBy){}
}
