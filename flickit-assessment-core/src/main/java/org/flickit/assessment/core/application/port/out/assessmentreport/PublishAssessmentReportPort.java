package org.flickit.assessment.core.application.port.out.assessmentreport;

import java.time.LocalDateTime;
import java.util.UUID;

public interface PublishAssessmentReportPort {

    void publish(Param param);

    record Param(UUID assessmentReportId,
                 LocalDateTime lastModificationTime,
                 UUID lastModifiedBy) {
    }
}