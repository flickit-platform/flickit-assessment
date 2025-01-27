package org.flickit.assessment.core.application.port.out.assessmentreport;

import org.flickit.assessment.core.application.domain.AssessmentReportMetadata;

import java.time.LocalDateTime;
import java.util.UUID;

public interface UpdateAssessmentReportPort {

    void update(Param param);

    record Param(UUID id,
                 AssessmentReportMetadata reportMetadata,
                 LocalDateTime lastModificationTime,
                 UUID lastModifiedBy) {
    }
}
