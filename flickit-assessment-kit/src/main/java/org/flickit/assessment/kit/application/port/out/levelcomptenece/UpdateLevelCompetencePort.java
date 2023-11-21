package org.flickit.assessment.kit.application.port.out.levelcomptenece;

public interface UpdateLevelCompetencePort {

    void update(Long affectedLevelId, Long effectiveLevelId, Integer value);
}
