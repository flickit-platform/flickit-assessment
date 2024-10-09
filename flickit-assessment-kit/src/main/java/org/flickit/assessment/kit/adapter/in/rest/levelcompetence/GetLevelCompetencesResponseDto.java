package org.flickit.assessment.kit.adapter.in.rest.levelcompetence;

import org.flickit.assessment.kit.application.port.in.levelcompetence.GetLevelCompetencesUseCase.LevelWithCompetencesListItem;

import java.util.List;

public record GetLevelCompetencesResponseDto(List<LevelWithCompetencesListItem> items) {
}
