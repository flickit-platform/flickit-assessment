package org.flickit.assessment.users.application.port.out.space;

import org.flickit.assessment.users.application.domain.Space;

import java.util.UUID;

public interface LoadSpacePort {

    boolean checkIsDefault(long spaceId);

    UUID loadOwnerId(long spaceId);

    Result loadById(long id);

    record Result(Space space, int membersCount, int assessmentsCount) {
    }
}
