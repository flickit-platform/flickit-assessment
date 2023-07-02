package org.flickit.flickitassessmentcore.application.port.out.assessmentresult;

import org.flickit.flickitassessmentcore.domain.AssessmentResult;

import java.util.Set;
import java.util.UUID;

public interface LoadAssessmentResultByAssessmentPort {

    Set<AssessmentResult> loadAssessmentResultByAssessmentId(UUID assessmentId);
}
