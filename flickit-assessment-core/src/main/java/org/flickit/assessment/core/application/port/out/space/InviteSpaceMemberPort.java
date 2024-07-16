package org.flickit.assessment.core.application.port.out.space;

import java.time.LocalDateTime;
import java.util.UUID;

public interface InviteSpaceMemberPort {

    void invite(Param param);

    record Param(long spaceId, String email, UUID createdBy,
                 LocalDateTime creationTime, LocalDateTime expirationDate) {
    }
}
