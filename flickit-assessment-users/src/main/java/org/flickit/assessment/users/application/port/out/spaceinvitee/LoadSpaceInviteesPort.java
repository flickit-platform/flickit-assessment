package org.flickit.assessment.users.application.port.out.spaceinvitee;

import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.users.application.domain.SpaceInvitee;

public interface LoadSpaceInviteesPort {

    PaginatedResponse<SpaceInvitee> loadInvitees(long spaceId, int page, int size);
}
