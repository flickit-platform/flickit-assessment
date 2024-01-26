package org.flickit.assessment.kit.application.port.out.kitdsl;

import java.time.Duration;

public interface LoadDslFilePathPort {

    String loadDslFilePath(Long kitId, Duration expiryDuration);
}
