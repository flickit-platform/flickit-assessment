package org.flickit.flickitassessmentcore.application.port.out.assessmentsubjectvalue;

import java.util.List;
import java.util.UUID;

public interface CreateAssessmentSubjectValuePort {

    void persistAllWithAssessmentResultId(List<Param> params, UUID assessmentResultId);

    record Param(Long assessmentSubjectId) {
    }
}
