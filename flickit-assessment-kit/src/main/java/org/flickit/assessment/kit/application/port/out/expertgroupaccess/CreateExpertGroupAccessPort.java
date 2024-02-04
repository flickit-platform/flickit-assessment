package org.flickit.assessment.kit.application.port.out.expertgroupaccess;

import java.time.LocalDate;
import java.util.UUID;

public interface CreateExpertGroupAccessPort {

    Long persist(Param param);

    record Param(long expertGroupId,
                 LocalDate inviteExpirationDate,
                 UUID userId,
                 String status) {
    }
}
