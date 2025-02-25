package org.flickit.assessment.users.application.port.out.space;

import java.util.UUID;

public interface CountSpacePort {

    int countBasicSpaces(UUID userId);
}
