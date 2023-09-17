package org.flickit.flickitassessmentcore.application.port.out.subject;

import org.flickit.flickitassessmentcore.application.domain.AssessmentResult;

import java.util.UUID;

public interface LoadSubjectReportInfoPort {

    AssessmentResult load(UUID assessmentId, Long subjectId);
}
