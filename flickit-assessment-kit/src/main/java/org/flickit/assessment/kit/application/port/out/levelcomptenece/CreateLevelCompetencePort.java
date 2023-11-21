package org.flickit.assessment.kit.application.port.out.levelcomptenece;

public interface CreateLevelCompetencePort {

    Long persist(Long affectedLevelId, Long effectiveLevelId, int value);
}
