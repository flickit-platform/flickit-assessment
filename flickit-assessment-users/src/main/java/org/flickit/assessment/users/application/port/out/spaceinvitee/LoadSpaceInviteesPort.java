package org.flickit.assessment.users.application.port.out.spaceinvitee;

import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;

import java.time.LocalDateTime;
import java.util.UUID;

public interface LoadSpaceInviteesPort {

    PaginatedResponse<Invitee> loadInvitees(long spaceId, int page, int size);

    record Invitee(UUID id, long spaceId, String email, LocalDateTime expirationDate,
                   LocalDateTime creationTime, UUID createdBy){}
}
