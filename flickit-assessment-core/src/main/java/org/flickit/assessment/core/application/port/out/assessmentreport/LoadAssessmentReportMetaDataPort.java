package org.flickit.assessment.core.application.port.out.assessmentreport;

import org.flickit.assessment.core.application.domain.AssessmentReportMetadata;

import java.util.UUID;

public interface LoadAssessmentReportMetaDataPort {

    AssessmentReportMetadata loadMetaData(UUID assessmentId);
}
