package org.flickit.assessment.users.application.port.out.spaceuseraccess;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface CreateSpaceUserAccessPort {

    void createAccess(List<Param> param);

    record Param(long spaceId, UUID userid, LocalDateTime creationTime, UUID createdBy){
    }
}
