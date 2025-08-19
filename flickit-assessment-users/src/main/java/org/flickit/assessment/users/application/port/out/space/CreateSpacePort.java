package org.flickit.assessment.users.application.port.out.space;

import org.flickit.assessment.common.application.domain.space.SpaceType;
import org.flickit.assessment.common.application.domain.space.SpaceStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public interface CreateSpacePort {

    long persist(Param param);

    record Param(String code,
                 String title,
                 SpaceType type,
                 SpaceStatus status,
                 LocalDateTime subscriptionExpiry,
                 boolean isDefault,
                 UUID createdBy,
                 LocalDateTime creationTime) {
    }
}
