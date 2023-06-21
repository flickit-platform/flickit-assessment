package org.flickit.flickitassessmentcore.application.port.out.assessment;

import org.flickit.flickitassessmentcore.domain.Assessment;

import java.util.UUID;

public interface LoadAssessmentPort {

    Assessment loadAssessment(UUID assessmentId);
}
