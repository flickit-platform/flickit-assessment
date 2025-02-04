package org.flickit.assessment.core.application.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class SubjectInsight {

    private final UUID assessmentResultId;
    private final Long subjectId;
    private final String insight;
    private final LocalDateTime insightTime;
    private final LocalDateTime lastModificationTime;
    private final UUID insightBy;
    private final boolean approved;
}
