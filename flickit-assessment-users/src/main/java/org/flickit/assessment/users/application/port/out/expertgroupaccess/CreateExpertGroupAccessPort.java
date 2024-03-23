package org.flickit.assessment.users.application.port.out.expertgroupaccess;

import org.flickit.assessment.users.application.domain.ExpertGroupAccessStatus;

import java.util.UUID;

public interface CreateExpertGroupAccessPort {

    void persist(Param param);

    record Param(long expertGroupId,
                 UUID userId,
                 ExpertGroupAccessStatus status) {
    }
}
