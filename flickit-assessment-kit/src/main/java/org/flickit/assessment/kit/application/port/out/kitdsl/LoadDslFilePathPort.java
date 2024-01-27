package org.flickit.assessment.kit.application.port.out.kitdsl;

import java.util.Optional;
import java.util.UUID;

public interface LoadDslFilePathPort {

    Optional<String> loadDslFilePath(Long kitId, UUID currentUserId);
}
