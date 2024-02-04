package org.flickit.assessment.advice.application.port.out.assessment;

import java.util.UUID;

public interface UserAssessmentAccessibilityPort {

    boolean hasAccess(UUID assessmentId, UUID userId);
}
