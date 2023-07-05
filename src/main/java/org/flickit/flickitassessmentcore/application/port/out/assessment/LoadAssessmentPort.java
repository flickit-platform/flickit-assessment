package org.flickit.flickitassessmentcore.application.port.out.assessment;

import org.flickit.flickitassessmentcore.domain.Assessment;

import java.util.UUID;

public interface LoadAssessmentPort {

    Result loadAssessment(Param param);

    record Param(UUID id) {}

    record Result(Assessment assessment) {}
}
