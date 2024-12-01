package org.flickit.assessment.core.application.domain.report;

import org.flickit.assessment.core.application.domain.MaturityLevel;

public record SubjectReportItem(Long id,
                                String title,
                                String description,
                                MaturityLevel maturityLevel,
                                Double confidenceValue,
                                boolean isCalculateValid,
                                boolean isConfidenceValid) {
}
