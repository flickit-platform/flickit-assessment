package org.flickit.assessment.core.adapter.in.rest.confidencelevel;

import org.flickit.assessment.core.application.port.in.confidencelevel.GetConfidenceLevelListUseCase;

import java.util.List;

public record GetConfidenceLevelListResponseDto(
    List<GetConfidenceLevelListUseCase.ConfidenceLevelItem> confidenceLevels) {
}
