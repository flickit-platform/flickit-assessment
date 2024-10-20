package org.flickit.assessment.kit.application.port.out.kituseraccess;

import java.util.UUID;

public interface CheckKitUserAccessPort {

    boolean hasAccess(Long kitId, UUID userId);
}
