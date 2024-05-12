package org.flickit.assessment.core.application.domain.report;

import org.flickit.assessment.core.application.domain.MaturityLevel;

public record AssessmentSubjectReportItem(
    Long id,
    String title,
    Integer index,
    String description,
    MaturityLevel maturityLevel) {
}
