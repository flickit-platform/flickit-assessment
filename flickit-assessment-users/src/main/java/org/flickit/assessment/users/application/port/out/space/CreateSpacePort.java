package org.flickit.assessment.users.application.port.out.space;

import org.flickit.assessment.users.application.domain.Space;

public interface CreateSpacePort {

    long persist(Space space);
}
