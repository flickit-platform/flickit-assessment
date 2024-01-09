package org.flickit.assessment.kit.application.port.out.expertgroupaccess;


import java.util.UUID;

public interface CreateExpertGroupAccessPort {

    Long persist(Param param);

    record Param(long expertGroupId,
                 String inviteEmail,
                 String inviteExpirationDate,
                 UUID userId) {
    }
}
