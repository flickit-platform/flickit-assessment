package org.flickit.assessment.users.application.port.out.spaceuseraccess;

import java.time.LocalDateTime;
import java.util.UUID;

public interface UpdateSpaceLastSeenPort {

    void updateLastSeen(long spaceId, UUID userId, LocalDateTime seenTime);
}
