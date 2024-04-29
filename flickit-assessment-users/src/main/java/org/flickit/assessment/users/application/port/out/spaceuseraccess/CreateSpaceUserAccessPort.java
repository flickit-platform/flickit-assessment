package org.flickit.assessment.users.application.port.out.spaceuseraccess;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface CreateSpaceUserAccessPort {

    void persist(Param param);

    void persistAll(List<Param> param);

    record Param(long spaceId, UUID userId, UUID createdBy, LocalDateTime creationTime) {
    }
}
