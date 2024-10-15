package org.flickit.assessment.kit.application.port.out.levelcomptenece;

public interface DeleteLevelCompetencePort {

    void delete(Long affectedLevelId, Long effectiveLevelId, Long kitVersionId);

    void delete(Long levelCompetenceId, Long kitVersionId);
}
