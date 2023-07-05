package org.flickit.flickitassessmentcore.adapter.in.rest.assessment;

import java.util.UUID;

public record EditAssessmentRequestDto(UUID id, String title, Long assessmentKitId, Integer colorId) {
}
