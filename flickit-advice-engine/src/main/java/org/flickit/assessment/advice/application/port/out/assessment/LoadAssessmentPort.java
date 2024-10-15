package org.flickit.assessment.advice.application.port.out.assessment;

import org.flickit.assessment.advice.application.domain.Assessment;

import java.util.UUID;

public interface LoadAssessmentPort {

    Assessment loadById (UUID assessmentId);
}
