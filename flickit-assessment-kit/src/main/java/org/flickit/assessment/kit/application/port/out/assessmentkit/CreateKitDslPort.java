package org.flickit.assessment.kit.application.port.out.assessmentkit;

public interface CreateKitDslPort {

    Long create(Param param);

    record Param(String zipFilePath, String jsonFilePath) {}

}
