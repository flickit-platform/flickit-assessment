package org.flickit.flickitassessmentcore.application.port.out.assessment;

public interface CountAssessmentsPort {

    Result countByKitId(Param param);

    record Param(Long assessmentKitId, Boolean deleted, Boolean notDeleted, Boolean total) {}

    record Result(Integer totalCount, Integer deletedCount, Integer notDeletedCount) {}

}
