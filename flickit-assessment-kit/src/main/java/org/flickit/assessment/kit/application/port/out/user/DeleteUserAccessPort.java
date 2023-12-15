package org.flickit.assessment.kit.application.port.out.user;

import java.util.UUID;

public interface DeleteUserAccessPort {

    void delete(Param param);

    record Param(Long kitId, UUID userId) {
    }
}
