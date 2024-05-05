package org.flickit.assessment.users.application.port.out;

import java.time.LocalDateTime;
import java.util.UUID;

public interface LoadSpaceDetailsPort {

    Result loadSpace(long id, UUID currentUserId);

    record Result (long id, String code, String title, UUID ownerId, LocalDateTime creationTime,
                   LocalDateTime lastModificationTime, UUID createdBy, UUID lastModifiedBy,
                   int membersCount, int assessmentsCount){
    }
}
