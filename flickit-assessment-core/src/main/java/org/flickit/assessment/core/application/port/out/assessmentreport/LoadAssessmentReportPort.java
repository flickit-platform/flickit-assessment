package org.flickit.assessment.core.application.port.out.assessmentreport;

import org.flickit.assessment.core.application.domain.AssessmentReport;

import java.util.Optional;
import java.util.UUID;

public interface LoadAssessmentReportPort {

    Optional<AssessmentReport> load(UUID assessmentId);
}
