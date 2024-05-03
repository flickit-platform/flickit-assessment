package org.flickit.assessment.core.application.domain.report;

import java.util.List;

public record SubjectReport(SubjectReportItem subject,
                            List<MaturityLevel> maturityLevels,
                            List<AttributeReportItem> attributes) {

    public record SubjectReportItem(Long id,
                                    String title,
                                    MaturityLevel maturityLevel,
                                    Double confidenceValue,
                                    boolean isCalculateValid,
                                    boolean isConfidenceValid) {}

    public record AttributeReportItem(Long id,
                                      int index,
                                      String title,
                                      String description,
                                      MaturityLevel maturityLevel,
                                      List<MaturityScore> maturityScores,
                                      Double confidenceValue) {

        public record MaturityScore(MaturityLevel maturityLevel, Double score) {}
    }

    public record MaturityLevel(long id, String title, int index, int value) {}
}
