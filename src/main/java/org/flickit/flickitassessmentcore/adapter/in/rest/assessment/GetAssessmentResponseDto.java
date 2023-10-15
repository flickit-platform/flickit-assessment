package org.flickit.flickitassessmentcore.adapter.in.rest.assessment;

import java.util.UUID;

public record GetAssessmentResponseDto(UUID assessmentId, String assessmentTitle, Long spaceId, Long kitId) {
}
