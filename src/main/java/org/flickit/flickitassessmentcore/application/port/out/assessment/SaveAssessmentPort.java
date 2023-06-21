package org.flickit.flickitassessmentcore.application.port.out.assessment;

import org.flickit.flickitassessmentcore.domain.Assessment;

public interface SaveAssessmentPort {

    Assessment saveAssessment(Assessment assessment);
}
