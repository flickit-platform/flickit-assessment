package org.flickit.assessment.kit.application.port.out.levelcomptenece;

import java.util.UUID;

public interface CreateLevelCompetencePort {

    Long persist(Long affectedLevelId, Long effectiveLevelId, int value, Long kitVersionId, UUID createdBy);
}
