package org.flickit.assessment.kit.application.port.out.kituseraccess;

import java.util.UUID;

public interface DeleteKitUserAccessPort {

    void delete(Param param);

    record Param(Long kitId, UUID userId) {
    }
}
