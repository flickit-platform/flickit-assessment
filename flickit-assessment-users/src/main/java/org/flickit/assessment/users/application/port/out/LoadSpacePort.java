package org.flickit.assessment.users.application.port.out;

import org.flickit.assessment.users.application.domain.Space;

import java.util.UUID;

public interface LoadSpacePort {

    Space loadSpace(long id, UUID currentUserId);
}
