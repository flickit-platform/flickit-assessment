package org.flickit.assessment.core.application.port.out.spaceuseraccess;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface CreateSpaceUserAccessPort {

    void persistByAssessmentId(CreateParam param);

    record CreateParam(UUID assessmentId,
                       UUID userId,
                       UUID createdBy,
                       LocalDateTime creationTime) {
    }

    void persistByUserIds(CreateAllParam param);

    record CreateAllParam(long spaceId,
                          List<UUID> userIds,
                          UUID createdBy,
                          LocalDateTime creationTime) {
    }
}
