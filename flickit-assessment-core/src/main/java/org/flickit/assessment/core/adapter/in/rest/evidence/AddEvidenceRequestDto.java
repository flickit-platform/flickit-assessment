package org.flickit.assessment.core.adapter.in.rest.evidence;

import java.util.UUID;

public record AddEvidenceRequestDto(String description, Long createdById, UUID assessmentId, Long questionId) {
}
