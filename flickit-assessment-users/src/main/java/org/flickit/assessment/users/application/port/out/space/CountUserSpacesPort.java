package org.flickit.assessment.users.application.port.out.space;

import org.flickit.assessment.common.application.domain.space.SpaceType;

import java.util.UUID;

public interface CountUserSpacesPort {

    int countUserSpaces(UUID userId, SpaceType type);
}
