package org.flickit.flickitassessmentcore.application.port.out.assessmentresult;

import org.flickit.flickitassessmentcore.domain.AssessmentResult;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface LoadAssessmentResultByAssessmentPort {

    Result loadAssessmentResultByAssessmentId(UUID assessmentId);

    record Result(List<AssessmentResult> results) {}
}
