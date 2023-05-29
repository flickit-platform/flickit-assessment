package org.flickit.flickitassessmentcore.application.port.out;

import org.flickit.flickitassessmentcore.domain.AssessmentKit;

public interface LoadAssessmentKitPort {

    AssessmentKit loadAssessmentKit(Long assessmentKitId);

}
