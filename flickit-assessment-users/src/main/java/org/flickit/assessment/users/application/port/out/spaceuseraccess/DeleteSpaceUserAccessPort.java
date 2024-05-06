package org.flickit.assessment.users.application.port.out.spaceuseraccess;

import java.util.UUID;

public interface DeleteSpaceUserAccessPort {

    void deleteAccess(long spaceId, UUID userId);
}
