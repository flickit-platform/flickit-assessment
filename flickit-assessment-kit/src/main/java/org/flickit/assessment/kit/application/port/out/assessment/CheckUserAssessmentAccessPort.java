package org.flickit.assessment.kit.application.port.out.assessment;

import java.util.UUID;

public interface CheckUserAssessmentAccessPort {

    boolean hasAccess(UUID assessmentId, UUID userId);
}
