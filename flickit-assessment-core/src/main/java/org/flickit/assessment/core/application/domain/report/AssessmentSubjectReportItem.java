package org.flickit.assessment.core.application.domain.report;

import org.flickit.assessment.core.application.domain.MaturityLevel;

import java.util.List;

public record AssessmentSubjectReportItem(
    Long id,
    String title,
    Integer index,
    String description,
    Double confidenceValue,
    MaturityLevel maturityLevel,
    List<AttributeReportItem> attributes) {
}
