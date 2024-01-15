package org.flickit.assessment.kit.application.port.out.kitdsl;

public interface CreateKitDslPort {

    Long create(String dslFilePath, String jsonFilePath);

}
