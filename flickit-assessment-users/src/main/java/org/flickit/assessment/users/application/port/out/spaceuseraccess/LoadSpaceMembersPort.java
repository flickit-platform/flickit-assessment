package org.flickit.assessment.users.application.port.out.spaceuseraccess;

import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;

import java.util.UUID;

public interface LoadSpaceMembersPort {

    PaginatedResponse<Member> loadSpaceMembers(long spaceId, int page, int size);

    record Member(UUID id, String email, String displayName, String bio, boolean isOwner, String picture, String linkedin) {
    }
}
