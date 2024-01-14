package org.flickit.assessment.kit.application.port.out.assessmentkit;

public interface CreateKitDslPort {

    Long create(String dslFilePath, String jsonFilePath);

}
