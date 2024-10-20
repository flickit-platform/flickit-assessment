package org.flickit.assessment.core.application.domain.report;

import org.flickit.assessment.core.application.domain.MaturityLevel;

import java.util.List;

public record SubjectAttributeReportItem(Long id,
                                         int index,
                                         String title,
                                         String description,
                                         MaturityLevel maturityLevel,
                                         List<MaturityScore> maturityScores,
                                         Double confidenceValue) {

    public record MaturityScore(MaturityLevel maturityLevel, Double score) {
    }
}
