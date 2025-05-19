package org.flickit.assessment.core.application.port.out.assessmentreport;

import org.flickit.assessment.core.application.domain.AssessmentReportMetadata;

import java.time.LocalDateTime;
import java.util.UUID;

public interface CreateAssessmentReportPort {

    /**
     * Persists a new assessment report in an UNPUBLISHED and RESTRICTED state.
     *
     * @param param
     */
    void persist(Param param);

    /**
     * Persists a new quick assessment report in an UNPUBLISHED and RESTRICTED state.
     *
     * @param param
     */
    void persist(QuickAssessmentReportParam param);

    record Param(UUID assessmentResultId,
                 AssessmentReportMetadata metadata,
                 LocalDateTime creationTime,
                 UUID createdBy) {
    }

    record QuickAssessmentReportParam(UUID assessmentResultId,
                                      LocalDateTime creationTime,
                                      UUID createdBy) {
    }
}
