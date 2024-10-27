package org.flickit.assessment.core.application.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class Assessment {

    private final UUID id;
    private final String code;
    private final String title;
    private final String shortTitle;
    private final AssessmentKit assessmentKit;
    private final Space space;
    private final LocalDateTime creationTime;
    private final LocalDateTime lastModificationTime;
    private final long deletionTime;
    private final boolean deleted;
    private final UUID createdBy;
}
