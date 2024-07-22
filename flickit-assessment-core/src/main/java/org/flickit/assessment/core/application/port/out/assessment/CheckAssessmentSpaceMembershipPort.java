package org.flickit.assessment.core.application.port.out.assessment;

import java.util.UUID;

public interface CheckAssessmentSpaceMembershipPort {

    boolean isAssessmentSpaceMember(UUID assessmentId, UUID userId);
}
