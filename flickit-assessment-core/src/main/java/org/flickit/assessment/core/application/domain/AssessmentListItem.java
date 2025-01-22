package org.flickit.assessment.core.application.domain;

import java.time.LocalDateTime;
import java.util.UUID;

public record AssessmentListItem(UUID id,
                                 String title,
                                 Kit kit,
                                 Space space,
                                 LocalDateTime lastModificationTime,
                                 MaturityLevel maturityLevel,
                                 Double confidenceValue,
                                 boolean isCalculateValid,
                                 boolean isConfidenceValid,
                                 Boolean manageable,
                                 boolean hasReport) {

    public record Kit(long id, String title, int maturityLevelsCount) {
    }

    public record Space(long id, String title) {
    }

    public record MaturityLevel(long id, String title, int value, int index) {
    }
}
