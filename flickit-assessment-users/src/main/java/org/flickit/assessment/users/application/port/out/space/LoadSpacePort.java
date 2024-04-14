package org.flickit.assessment.users.application.port.out.space;

import org.flickit.assessment.users.application.domain.Space;

public interface LoadSpacePort {

    Space loadSpace(long spaceId);

}
