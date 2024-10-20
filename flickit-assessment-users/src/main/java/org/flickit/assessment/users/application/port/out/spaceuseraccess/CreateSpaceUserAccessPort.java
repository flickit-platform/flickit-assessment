package org.flickit.assessment.users.application.port.out.spaceuseraccess;

import org.flickit.assessment.users.application.domain.SpaceUserAccess;

import java.util.List;

public interface CreateSpaceUserAccessPort {

    void persist(SpaceUserAccess access);

    void persistAll(List<SpaceUserAccess> accesses);
}
