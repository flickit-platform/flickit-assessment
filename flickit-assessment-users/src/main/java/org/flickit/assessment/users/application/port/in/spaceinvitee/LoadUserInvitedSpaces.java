package org.flickit.assessment.users.application.port.in.spaceinvitee;

import java.util.List;
import java.util.UUID;

public interface LoadUserInvitedSpaces {

    List<Result> loadSpacesIds(String email);

    record Result(long spaceId, UUID createdBy){}
}
