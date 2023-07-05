package org.flickit.flickitassessmentcore.application.port.out.subjectvalue;

import java.util.List;
import java.util.UUID;

public interface CreateSubjectValuePort {

    void persistAllWithAssessmentResultId(List<Param> params, UUID assessmentResultId);

    record Param(Long subjectId) {
    }
}
