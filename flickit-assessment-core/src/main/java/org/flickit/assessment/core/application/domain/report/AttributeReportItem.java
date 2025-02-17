package org.flickit.assessment.core.application.domain.report;

import org.flickit.assessment.core.application.domain.MaturityLevel;

public record AttributeReportItem(
    long id,
    String title,
    String description,
    String insight,
    int index,
    int weight,
    Double confidenceValue,
    MaturityLevel maturityLevel) {
}
