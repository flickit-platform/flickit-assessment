package org.flickit.assessment.users.application.port.out.expertgroupaccess;

import org.flickit.assessment.data.jpa.users.expertgroupaccess.ExpertGroupAccessStatus;

import java.util.UUID;

public interface CreateExpertGroupAccessPort {

    Long persist(Param param);

    record Param(long expertGroupId,
                 UUID userId,
                 ExpertGroupAccessStatus status) {
    }
}
