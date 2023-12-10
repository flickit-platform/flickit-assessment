package org.flickit.assessment.core.adapter.in.rest.assessment;

import org.flickit.assessment.core.application.port.in.assessment.CalculateConfidenceUseCase.Result;

public record CalculateConfidenceResponseDto(Result confidence) {
}
