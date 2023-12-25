package org.flickit.assessment.kit.application.port.out.kituseraccess;

import java.util.UUID;

public interface GrantUserAccessToKitPort {

    boolean grantUserAccess(Long kitId, UUID userId);
}
