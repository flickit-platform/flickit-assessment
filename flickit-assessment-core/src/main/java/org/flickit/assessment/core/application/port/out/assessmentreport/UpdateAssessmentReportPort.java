package org.flickit.assessment.core.application.port.out.assessmentreport;

import org.flickit.assessment.core.application.domain.AssessmentReportMetadata;

import java.util.UUID;

public interface UpdateAssessmentReportPort {

    void update(UUID id, AssessmentReportMetadata assessmentReport);
}
