package org.flickit.assessment.users.application.port.out.expertgroupaccess;

import java.time.LocalDateTime;
import java.util.UUID;

public interface UpdateExpertGroupLastSeenPort {

    void updateLastSeen(long expertGroupId, UUID userId, LocalDateTime currentTime);
}
