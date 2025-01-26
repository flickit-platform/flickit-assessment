package org.flickit.assessment.core.application.domain.report;

import org.flickit.assessment.core.application.domain.MaturityLevel;

public record AttributeReportItem(
    long id,
    String title,
    String description,
    String insight,
    int index,
    Double confidenceValue,
    MaturityLevel maturityLevel) {
}
