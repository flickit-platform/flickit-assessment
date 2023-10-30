package org.flickit.assessment.core.application.port.out.subject;

import org.flickit.assessment.core.application.domain.AssessmentResult;

import java.util.UUID;

public interface LoadSubjectReportInfoPort {

    AssessmentResult load(UUID assessmentId, Long subjectId);
}
