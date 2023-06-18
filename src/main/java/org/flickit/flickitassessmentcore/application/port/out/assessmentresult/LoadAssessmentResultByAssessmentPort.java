package org.flickit.flickitassessmentcore.application.port.out.assessmentresult;

import org.flickit.flickitassessmentcore.domain.AssessmentResult;

import java.util.Set;
import java.util.UUID;

public interface LoadAssessmentResultByAssessmentPort {

    public Set<AssessmentResult> loadAssessmentResultByAssessmentId(UUID assessmentId);
}
