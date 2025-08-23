package org.flickit.assessment.kit.application.port.out.assessmentkit;

import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;

import java.util.UUID;

public interface LoadKitUsersPort {

    PaginatedResponse<KitUser> loadKitUsers(Param param);

    record Param(Long kitId, int page, int size) {
    }

    record KitUser(UUID id,
                   String displayName,
                   String email,
                   String picturePath) {
    }
}
