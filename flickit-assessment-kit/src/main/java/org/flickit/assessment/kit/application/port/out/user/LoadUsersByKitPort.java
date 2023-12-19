package org.flickit.assessment.kit.application.port.out.user;

import org.flickit.assessment.kit.application.domain.crud.KitUserPaginatedResponse;

public interface LoadUsersByKitPort {

    KitUserPaginatedResponse load(Param param);

    record Param(Long kitId, int page, int size) {
    }
}
