package org.flickit.assessment.kit.application.port.out.assessmentkit;

public interface CreateAssessmentKitDslPort {

    Long create(Param param);

    record Param(String zipFileUrl, String zipFileVersionId, String jsonFileUrl,String jsonFileVersionId, String filePath) {}
}
