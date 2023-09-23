package org.flickit.flickitassessmentcore.application.port.out.assessment;

import java.util.UUID;

public interface GetAssessmentSpacePort {

    Long getSpaceIdByAssessmentId(UUID assessmentId);
}
