package org.flickit.flickitassessmentcore.application.port.out.assessment;

import org.flickit.flickitassessmentcore.domain.Assessment;

import java.util.UUID;

public interface SaveAssessmentPort {

    Result saveAssessment(Param param);

    record Param(Assessment assessment) {}

    record Result(UUID id) {}
}
