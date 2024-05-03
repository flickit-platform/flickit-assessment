package org.flickit.assessment.core.application.port.out.subject;

import org.flickit.assessment.core.application.domain.report.SubjectReport;

import java.util.UUID;

public interface LoadSubjectReportInfoPort {

    SubjectReport load(UUID assessmentId, Long subjectId);
}
