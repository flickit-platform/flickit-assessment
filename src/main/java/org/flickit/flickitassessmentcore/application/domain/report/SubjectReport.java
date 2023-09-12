package org.flickit.flickitassessmentcore.application.domain.report;

import java.util.List;

public record SubjectReport(SubjectReportItem subject,
                            List<TopAttributeItem> topStrengths,
                            List<TopAttributeItem> topWeaknesses,
                            List<AttributeReportItem> attributes) {

    public record SubjectReportItem(Long id,
                                    Long maturityLevelId,
                                    boolean isCalculateValid) {
    }

    public record TopAttributeItem(Long id) {
    }

    public record AttributeReportItem(Long id, Long maturityLevelId) {
    }
}
