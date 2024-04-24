package org.flickit.assessment.users.application.port.out.space;

import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;

import java.time.LocalDateTime;
import java.util.UUID;

public interface LoadSpaceListPort {

    PaginatedResponse<Result> loadSpaceList(Param param);

    record Param(int size, int page, UUID currentUserId) {
    }

    record Result(Long id, String code, String title, UUID ownerId,
                  LocalDateTime lastModificationTime, int membersCount, int assessmentsCount) {
    }
}
