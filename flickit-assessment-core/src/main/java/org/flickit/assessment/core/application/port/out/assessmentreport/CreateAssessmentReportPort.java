package org.flickit.assessment.core.application.port.out.assessmentreport;

import org.flickit.assessment.core.application.domain.AssessmentReportMetadata;
import org.flickit.assessment.core.application.domain.VisibilityType;

import java.time.LocalDateTime;
import java.util.UUID;

public interface CreateAssessmentReportPort {

    /**
     * Persists a new assessment report with the specified parameters.
     *
     * @param param
     */
    void persist(Param param);

    record Param(UUID assessmentResultId,
                 AssessmentReportMetadata metadata,
                 boolean published,
                 VisibilityType visibilityType,
                 LocalDateTime creationTime,
                 UUID createdBy) {
    }
}
