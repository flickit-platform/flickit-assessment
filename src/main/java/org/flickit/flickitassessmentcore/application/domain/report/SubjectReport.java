package org.flickit.flickitassessmentcore.application.domain.report;

import java.util.List;

public record SubjectReport(SubjectReportItem subject,
                            List<AttributeReportItem> topStrengths,
                            List<AttributeReportItem> topWeaknesses,
                            List<QualityAttributeReportItem> attributes) {

    public record SubjectReportItem(Long id,
                                    Long maturityLevelId,
                                    boolean isCalculateValid) {
    }

    public record AttributeReportItem(Long id) {
    }

    public record QualityAttributeReportItem(Long id, Long maturityLevelId) {
    }
}
