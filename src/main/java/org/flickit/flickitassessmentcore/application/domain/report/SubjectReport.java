package org.flickit.flickitassessmentcore.application.domain.report;

import java.util.List;

public record SubjectReport(SubjectReportItem subject,
                            List<TopAttribute> topStrengths,
                            List<TopAttribute> topWeaknesses,
                            List<AttributeReportItem> attributes) {

    public record SubjectReportItem(Long id,
                                    Long maturityLevelId,
                                    boolean isCalculateValid) {
    }

    public record AttributeReportItem(Long id, Long maturityLevelId) {
    }
}
