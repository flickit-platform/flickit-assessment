package org.flickit.flickitassessmentcore.application.port.out.assessmentresult;

import java.util.UUID;

public interface CreateAssessmentResultPort {

    UUID persist(Param param);

    record Param(UUID assessmentId, boolean isValid) {
    }
}
