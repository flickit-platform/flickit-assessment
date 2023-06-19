package org.flickit.assessment.core.application.port.out.assessment;

import org.flickit.assessment.core.domain.Assessment;

public interface SaveAssessmentPort {

    Assessment saveAssessment(Assessment assessment);
}
