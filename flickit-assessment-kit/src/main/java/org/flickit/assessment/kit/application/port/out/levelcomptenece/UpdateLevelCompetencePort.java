package org.flickit.assessment.kit.application.port.out.levelcomptenece;

public interface UpdateLevelCompetencePort {

    void update(Long competenceId, Long effectiveLevelId, Integer value);
}
