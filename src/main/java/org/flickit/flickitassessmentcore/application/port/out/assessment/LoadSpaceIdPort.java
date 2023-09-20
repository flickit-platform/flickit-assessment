package org.flickit.flickitassessmentcore.application.port.out.assessment;

import java.util.UUID;

public interface LoadSpaceIdPort {

    Long loadSpaceIdByAssessmentId(UUID assessmentId);
}
