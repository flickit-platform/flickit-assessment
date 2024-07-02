package org.flickit.assessment.users.application.port.out.space;

import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.users.application.domain.Space;

import java.util.UUID;

public interface LoadSpaceListPort {

    PaginatedResponse<Result> loadSpaceList(UUID currentUserId, int size, int page);

    record Result(Space space, String ownerName, int membersCount, int assessmentsCount) {
    }
}
