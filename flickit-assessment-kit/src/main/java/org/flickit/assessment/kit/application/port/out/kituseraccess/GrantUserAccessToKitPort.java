package org.flickit.assessment.kit.application.port.out.kituseraccess;

import java.util.UUID;

public interface GrantUserAccessToKitPort {

    void grantUserAccess(Long kitId, UUID userId);
}
