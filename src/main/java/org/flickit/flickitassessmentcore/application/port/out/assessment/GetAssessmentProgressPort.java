package org.flickit.flickitassessmentcore.application.port.out.assessment;

import java.util.UUID;

public interface GetAssessmentProgressPort {

    Result getAssessmentProgressById(UUID assessmentId);

    record Result(UUID id, int allAnswersCount) {
    }
}
