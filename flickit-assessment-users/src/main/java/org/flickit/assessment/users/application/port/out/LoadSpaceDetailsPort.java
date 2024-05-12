package org.flickit.assessment.users.application.port.out;

import org.flickit.assessment.users.application.domain.Space;

import java.util.UUID;

public interface LoadSpaceDetailsPort {

    Result loadSpace(long id, UUID currentUserId);

    record Result(Space space, int membersCount, int assessmentsCount) {
    }
}
