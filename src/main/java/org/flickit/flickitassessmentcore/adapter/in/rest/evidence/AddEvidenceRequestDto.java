package org.flickit.flickitassessmentcore.adapter.in.rest.evidence;

import java.util.UUID;

public record AddEvidenceRequestDto(UUID assessmentId, Long questionId, String description, Long createdById) {
}
