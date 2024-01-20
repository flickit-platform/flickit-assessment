package org.flickit.assessment.kit.application.port.out.kituseraccess;

import java.util.List;
import java.util.UUID;

public interface GrantUserAccessToKitPort {

    void grantUserAccess(Long kitId, UUID userId);

    void grantUsersAccess(Long kitId, List<UUID> userIds);
}
