package org.flickit.assessment.kit.application.port.out.levelcomptenece;

import java.util.UUID;

public interface UpdateLevelCompetencePort {

    void update(Long affectedLevelId, Long effectiveLevelId, Long kitVersionId, Integer value, UUID lastModifiedBy);

    void updateById(long id, long kitVersionId, int value, UUID lastModifiedBy);
}
