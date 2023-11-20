package org.flickit.assessment.kit.application.port.out.levelcomptenece;

public interface CreateLevelCompetencePort {

    Long persist(Long effectiveLevelId, Integer value, String maturityLevelCode, Long kitId);
}
