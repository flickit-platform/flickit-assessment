package org.flickit.assessment.core.application.domain.report;

import org.flickit.assessment.core.application.domain.MaturityLevel;

public record AttributeReportItem(
    long id,
    String title,
    int index,
    MaturityLevel maturityLevel) {
}
