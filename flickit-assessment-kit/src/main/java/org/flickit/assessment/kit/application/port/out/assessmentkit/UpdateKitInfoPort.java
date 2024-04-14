package org.flickit.assessment.kit.application.port.out.assessmentkit;

import org.flickit.assessment.kit.application.port.in.assessmentkit.UpdateKitInfoUseCase.Result;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public interface UpdateKitInfoPort {

    Result update(Param param);

    record Param(Long kitId,
                 String title,
                 String summary,
                 Boolean published,
                 Boolean isPrivate,
                 Double price,
                 String about,
                 Set<Long> tags,
                 UUID currentUserId,
                 LocalDateTime lastModificationTime) {
    }
}
