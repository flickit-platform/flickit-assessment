package org.flickit.assessment.kit.application.port.out.kitdsl;

import java.time.Duration;
import java.util.UUID;

public interface LoadDslFilePathPort {

    String loadDslFilePath(Long kitId, UUID currentUserId, Duration expiryDuration);
}
