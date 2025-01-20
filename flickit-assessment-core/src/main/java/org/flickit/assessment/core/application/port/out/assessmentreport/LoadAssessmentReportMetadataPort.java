package org.flickit.assessment.core.application.port.out.assessmentreport;

import java.util.UUID;

public interface LoadAssessmentReportMetadataPort {

    String load(UUID assessmentId);
}
