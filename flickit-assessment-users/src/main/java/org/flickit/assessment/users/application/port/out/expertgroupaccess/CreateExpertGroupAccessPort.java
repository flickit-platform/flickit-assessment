package org.flickit.assessment.users.application.port.out.expertgroupaccess;


import java.time.LocalDate;
import java.util.UUID;

public interface CreateExpertGroupAccessPort {

    Long persist(Param param);

    record Param(long expertGroupId,
                 String inviteEmail,
                 LocalDate inviteExpirationDate,
                 UUID userId) {
    }
}
