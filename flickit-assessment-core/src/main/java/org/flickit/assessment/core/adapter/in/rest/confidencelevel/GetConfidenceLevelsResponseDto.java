package org.flickit.assessment.core.adapter.in.rest.confidencelevel;

import org.flickit.assessment.core.application.port.in.confidencelevel.GetConfidenceLevelsUseCase.ConfidenceLevelItem;

import java.util.List;

public record GetConfidenceLevelsResponseDto(
    ConfidenceLevelItem defaultConfidenceLevel, List<ConfidenceLevelItem> confidenceLevels) {
}
