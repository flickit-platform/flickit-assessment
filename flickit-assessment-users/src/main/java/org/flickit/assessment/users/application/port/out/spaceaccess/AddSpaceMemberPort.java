package org.flickit.assessment.users.application.port.out.spaceaccess;

import java.time.LocalDateTime;
import java.util.UUID;

public interface AddSpaceMemberPort {

    void persist(Param param);

    record Param(long spaceId, UUID invitee, UUID inviter, LocalDateTime creationTime){
    }
}
