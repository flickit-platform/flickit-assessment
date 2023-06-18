package org.flickit.flickitassessmentcore.application.port.out.assessmentkit;

import org.flickit.flickitassessmentcore.domain.AssessmentKit;

public interface LoadAssessmentKitPort {

    AssessmentKit loadAssessmentKit(Long assessmentKitId);

}
