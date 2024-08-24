package org.flickit.assessment.core.application.domain;

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
    private final UUID insightBy;
}
