package org.flickit.assessment.kit.application.port.out.assessmentkit;

public interface CreateAssessmentKitDslPort {

    Result create(Param param);

    record Param(String zipFilePath, String jsonFilePath) {}

    record Result(Long kitZipDslId, Long kitJsonDslId) {}
}
