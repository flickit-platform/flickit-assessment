package org.flickit.flickitassessmentcore.application.port.out.assessment;

import org.flickit.flickitassessmentcore.domain.Assessment;

import java.util.UUID;

public interface LoadAssessmentPort {

    Result loadAssessment(UUID id);

    record Result(Assessment assessment) {}
}
