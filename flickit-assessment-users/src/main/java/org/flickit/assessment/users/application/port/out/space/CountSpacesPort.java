package org.flickit.assessment.users.application.port.out.space;

import java.util.UUID;

public interface CountSpacesPort {

    int countBasicSpaces(UUID ownerId);
}
