package org.flickit.assessment.core.application.domain;

import java.time.LocalDateTime;
import java.util.UUID;

public record AssessmentListItem(UUID id,
                                 String title,
                                 Kit kit,
                                 Space space,
                                 AssessmentColor color,
                                 LocalDateTime lastModificationTime,
                                 MaturityLevel maturityLevel,
                                 boolean isCalculateValid,
                                 boolean isConfidenceValid,
                                 boolean manageable,
                                 boolean viewable) {

    public record Kit(long id, String title, int maturityLevelsCount) {
    }

    public record Space(long id, String title) {
    }

    public record MaturityLevel(long id, String title, int value, int index) {
    }
}
