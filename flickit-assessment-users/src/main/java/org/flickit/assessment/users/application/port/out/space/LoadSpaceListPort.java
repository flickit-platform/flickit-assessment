package org.flickit.assessment.users.application.port.out.space;

import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.users.application.domain.Space;

import java.util.UUID;

public interface LoadSpaceListPort {

    PaginatedResponse<Result> loadSpaceList(Param param);

    record Param(int size, int page, UUID currentUserId) {
    }

    record Result(Space space, int membersCount, int assessmentsCount) {
    }
}
