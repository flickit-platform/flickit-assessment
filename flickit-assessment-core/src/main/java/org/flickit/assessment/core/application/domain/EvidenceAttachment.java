package org.flickit.assessment.core.application.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class EvidenceAttachment {

    private final UUID id;
    private final UUID evidenceId;
    private final String filePath;
    private final String description;
    private final UUID createdBy;
    private final LocalDateTime creationTime;
}
