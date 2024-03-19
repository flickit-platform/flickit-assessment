package org.flickit.assessment.users.application.port.out.expertgroupaccess;


import java.time.LocalDateTime;
import java.util.UUID;

public interface CreateExpertGroupAccessPort {

    void persist(Param param);

    record Param(long expertGroupId,
                 String inviteEmail,
                 LocalDateTime inviteExpirationDate,
                 UUID userId) {
    }
}
