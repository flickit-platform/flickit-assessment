package org.flickit.assessment.kit.application.port.out.assessmentkit;

import org.flickit.assessment.kit.application.domain.crud.KitUserPaginatedResponse;

public interface LoadKitUsersPort {

    KitUserPaginatedResponse loadKitUsers(Param param);

    record Param(Long kitId, int page, int size) {
    }
}
