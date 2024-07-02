package org.flickit.assessment.core.application.port.out.assessmentkit;

import java.util.Optional;
import java.util.UUID;

public interface CheckKitAccessPort {

    Optional<Long> checkAccess(long kitId, UUID userId);
}
