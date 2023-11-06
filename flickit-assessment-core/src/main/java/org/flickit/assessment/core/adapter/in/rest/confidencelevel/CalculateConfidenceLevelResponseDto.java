package org.flickit.assessment.core.adapter.in.rest.confidencelevel;

import org.flickit.assessment.core.application.port.in.confidencelevel.CalculateConfidenceLevelUseCase.ConfidenceLevelResult;

public record CalculateConfidenceLevelResponseDto(ConfidenceLevelResult confidenceLevel) {
}
