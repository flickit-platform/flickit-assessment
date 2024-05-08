package org.flickit.assessment.core.application.domain.report;

public record AttributeReportItem(
    long id,
    String title,
    int index,
    int maturityLevelIndex) {
}
