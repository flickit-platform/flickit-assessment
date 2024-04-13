package org.flickit.assessment.kit.application.port.out.assessmentkit;

import org.flickit.assessment.kit.application.port.in.assessmentkit.EditKitInfoUseCase;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface UpdateKitInfoPort {

    EditKitInfoUseCase.Result update(Param param);

    record Param(Long assessmentKitId,
                 String title,
                 String summary,
                 Boolean isActive,
                 Boolean isPrivate,
                 Double price,
                 String about,
                 List<Long> tags,
                 UUID currentUserId,
                 LocalDateTime lastModificationTime) {
    }
}
