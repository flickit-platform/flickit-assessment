package org.flickit.assessment.core.application.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class EvidenceAttachment {
    private final UUID id;
    private final UUID evidenceId;
    private final String file;
    private final String description;
}
