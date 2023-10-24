package org.flickit.flickitassessmentcore.application.domain.report;

import org.flickit.flickitassessmentcore.application.domain.MaturityScore;

import java.util.List;
import java.util.Set;

public record SubjectReport(SubjectReportItem subject,
                            List<TopAttribute> topStrengths,
                            List<TopAttribute> topWeaknesses,
                            List<AttributeReportItem> attributes) {

    public record SubjectReportItem(Long id,
                                    Long maturityLevelId,
                                    boolean isCalculateValid) {
    }

    public record AttributeReportItem(Long id, Set<MaturityScore> maturityScores) {
    }
}
