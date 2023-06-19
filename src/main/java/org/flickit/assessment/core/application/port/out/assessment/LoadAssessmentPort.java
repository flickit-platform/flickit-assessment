package org.flickit.assessment.core.application.port.out.assessment;

import org.flickit.assessment.core.domain.Assessment;

import java.util.UUID;

public interface LoadAssessmentPort {

    Assessment loadAssessment(UUID assessmentId);
}
