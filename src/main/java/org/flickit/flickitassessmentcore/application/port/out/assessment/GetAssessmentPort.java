package org.flickit.flickitassessmentcore.application.port.out.assessment;

import org.flickit.flickitassessmentcore.application.domain.Assessment;

import java.util.UUID;

public interface GetAssessmentPort {

    Assessment getAssessmentById(UUID assessmentId);
}
