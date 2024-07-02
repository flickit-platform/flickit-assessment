package org.flickit.assessment.common.application.domain.assessment;

import java.util.UUID;

public interface SpaceAccessChecker {

    boolean hasAccess(UUID assessmentId, UUID userId);
}
