package org.flickit.assessment.users.application.port.out.space;

import java.time.LocalDateTime;
import java.util.UUID;

public interface CreateSpacePort {

    void persist(Param param);

    record Param(String code, String title, UUID ownerId, LocalDateTime creationTime,
                 LocalDateTime lastModificationTime, UUID createdBy, UUID lastModifiedBy) {
    }
}
