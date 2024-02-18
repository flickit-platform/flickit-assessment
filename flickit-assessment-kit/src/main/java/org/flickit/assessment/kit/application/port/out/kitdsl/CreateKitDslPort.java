package org.flickit.assessment.kit.application.port.out.kitdsl;

import java.util.UUID;

public interface CreateKitDslPort {

    Long create(String dslFilePath, String jsonFilePath, UUID createdBy);

}
