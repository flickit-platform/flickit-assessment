package org.flickit.assessment.core.adapter.in.rest.confidencelevel;

import org.flickit.assessment.core.application.port.in.confidencelevel.CalculateConfidenceLevelUseCase.Result;

public record CalculateConfidenceLevelResponseDto(Result confidenceLevel) {
}
