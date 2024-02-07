package org.flickit.assessment.kit.application.port.out.expertgroupaccess;

import java.time.LocalDateTime;
import java.util.UUID;

public interface CreateExpertGroupAccessPort {

    Long persist(Param param);

    record Param(long expertGroupId,
                 LocalDateTime inviteExpirationDate,
                 UUID userId,
                 String status) {
    }
}
