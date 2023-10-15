package org.flickit.flickitassessmentcore.application.domain.crud;

import org.flickit.flickitassessmentcore.application.domain.AssessmentColor;

import java.time.LocalDateTime;
import java.util.UUID;

public record AssessmentListItem(UUID id,
                                 String title,
                                 Long assessmentKitId,
                                 Long spaceId,
                                 AssessmentColor color,
                                 LocalDateTime lastModificationTime,
                                 Long maturityLevelId,
                                 boolean isCalculateValid) {
}
