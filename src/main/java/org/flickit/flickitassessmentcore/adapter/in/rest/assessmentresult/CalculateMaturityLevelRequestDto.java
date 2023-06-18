package org.flickit.flickitassessmentcore.adapter.in.rest.assessmentresult;

import java.util.UUID;

public record CalculateMaturityLevelRequestDto(UUID resultId, Long qaId, Long subId) {
}
