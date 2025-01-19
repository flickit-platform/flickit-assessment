package org.flickit.assessment.core.application.port.out.assessmentreport;

import java.util.UUID;

public interface LoadAssessmentReportMetaDataPort {

    String loadMetadata(UUID assessmentId);
}
