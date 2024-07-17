package org.flickit.assessment.core.application.port.out.spaceuseraccess;

import org.flickit.assessment.core.application.domain.SpaceUserAccess;

public interface SpaceUserAccessPort {

    void persist(SpaceUserAccess access);
}
