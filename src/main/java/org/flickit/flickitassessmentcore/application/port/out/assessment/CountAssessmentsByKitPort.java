package org.flickit.flickitassessmentcore.application.port.out.assessment;

public interface CountAssessmentsByKitPort {

    Result count(Param param);

    record Param(Long assessmentKitId, Boolean deleted, Boolean notDeleted, Boolean total) {}

    record Result(Integer totalCount, Integer deletedCount, Integer notDeletedCount) {}

}
