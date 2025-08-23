package org.flickit.assessment.core.application.domain.insight;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class AssessmentInsight {

    private final UUID id;
    private final UUID assessmentResultId;
    private final String insight;
    private final LocalDateTime insightTime;
    private final LocalDateTime lastModificationTime;
    private final UUID insightBy;
    private final boolean approved;
}
