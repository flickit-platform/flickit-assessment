package org.flickit.assessment.kit.application.port.out.levelcomptenece;

import java.time.LocalDateTime;
import java.util.UUID;

public interface UpdateLevelCompetencePort {

    void update(Long affectedLevelId, Long effectiveLevelId, Long kitVersionId, Integer value, UUID lastModifiedBy);

    void updateInfo(Param param);

    record Param(long id, long kitVersionId, int value, UUID lastModifiedBy, LocalDateTime lastModificationTime){
    }
}
