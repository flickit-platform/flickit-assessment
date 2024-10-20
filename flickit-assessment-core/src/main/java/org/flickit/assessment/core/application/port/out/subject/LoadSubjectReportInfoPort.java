package org.flickit.assessment.core.application.port.out.subject;

import org.flickit.assessment.core.application.domain.MaturityLevel;
import org.flickit.assessment.core.application.domain.report.SubjectAttributeReportItem;
import org.flickit.assessment.core.application.domain.report.SubjectReportItem;

import java.util.List;
import java.util.UUID;

public interface LoadSubjectReportInfoPort {

    Result load(UUID assessmentId, Long subjectId);

    record Result(
        SubjectReportItem subject,
        List<MaturityLevel> maturityLevels,
        List<SubjectAttributeReportItem> attributes) {
    }
}
