package org.flickit.assessment.users.application.port.out.user;

import java.time.LocalDateTime;
import java.util.UUID;

public interface CreateUserPort {

    UUID persist(Param param);

    record Param(UUID id,
                 String displayName,
                 String email,
                 LocalDateTime creationTime,
                 LocalDateTime lastModificationTime) {
    }
}
