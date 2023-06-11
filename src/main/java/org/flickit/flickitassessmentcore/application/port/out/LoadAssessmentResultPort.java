package org.flickit.flickitassessmentcore.application.port.out;

import org.flickit.flickitassessmentcore.domain.AssessmentResult;

import java.util.UUID;

public interface LoadAssessmentResultPort {

    public AssessmentResult loadResult(UUID resultId);
}
