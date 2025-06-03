package org.flickit.assessment.users.application.port.out.space;

import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.users.application.domain.Space;

import java.util.List;
import java.util.UUID;

public interface LoadSpaceListPort {

    /**
     * Loads a paginated list of spaces for the specified user.
     *
     * @param currentUserId The ID of the current user.
     * @param size          The number of items per page.
     * @param page          The page number (0-based).
     * @return A paginated response containing space information along with metadata.
     */
    PaginatedResponse<Result> loadSpaceList(UUID currentUserId, int size, int page);

    /**
     * Loads a list of spaces for the specified user.
     *
     * @param currentUserId The ID of the current user.
     * @return A list of the user's spaces based and assessment count.
     */
    List<SpaceWithAssessmentCount> loadSpaceList(UUID currentUserId);

    record Result(Space space,
                  String ownerName,
                  int membersCount,
                  int assessmentsCount) {
    }

    record SpaceWithAssessmentCount(Space space, int assessmentCount) {
    }
}
