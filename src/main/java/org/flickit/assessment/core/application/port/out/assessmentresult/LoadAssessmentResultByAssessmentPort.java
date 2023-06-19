package org.flickit.assessment.core.application.port.out.assessmentresult;

import org.flickit.assessment.core.domain.AssessmentResult;

import java.util.Set;
import java.util.UUID;

public interface LoadAssessmentResultByAssessmentPort {

    public Set<AssessmentResult> loadAssessmentResultByAssessmentId(UUID assessmentId);
}
