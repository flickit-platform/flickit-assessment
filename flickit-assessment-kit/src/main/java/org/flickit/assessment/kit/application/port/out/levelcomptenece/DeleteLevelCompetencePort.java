package org.flickit.assessment.kit.application.port.out.levelcomptenece;

public interface DeleteLevelCompetencePort {

    void delete(Long affectedLevelId, Long effectiveLevelId, Long kitVersionId);

    void deleteByIdAndKitVersionId(long id, long kitVersionId);
}
