package org.flickit.assessment.core.application.port.out.assessment;

public interface CountAssessmentsPort {

    Result count(Param param);

    record Param(Long kitId, Long spaceId, boolean deleted, boolean notDeleted, boolean total) {}

    record Result(Integer totalCount, Integer deletedCount, Integer notDeletedCount) {}
}
