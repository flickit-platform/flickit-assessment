package org.flickit.assessment.users.application.port.out.space;

import org.flickit.assessment.users.application.domain.Space;

public interface LoadSpaceDetailsPort {

    Result loadSpace(long id);

    record Result(Space space, int membersCount, int assessmentsCount) {
    }
}
