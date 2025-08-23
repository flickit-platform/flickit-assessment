package org.flickit.assessment.core.application.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class AssessmentReport {

    private final UUID id;
    private final UUID assessmentResultId;
    private final AssessmentReportMetadata metadata;
    private final boolean published;
    private final VisibilityType visibility;
    private final String linkHash;
    private final LocalDateTime creationTime;
    private final LocalDateTime lastModificationTime;
    private final UUID createdBy;
    private final UUID lastModifiedBy;
}
