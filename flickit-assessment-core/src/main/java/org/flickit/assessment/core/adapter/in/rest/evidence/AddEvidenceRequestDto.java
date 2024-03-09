package org.flickit.assessment.core.adapter.in.rest.evidence;

import java.util.UUID;

public record AddEvidenceRequestDto(
    String description,
    UUID assessmentId,
    UUID questionRefNum,
    String type
) {
}
