package org.flickit.flickitassessmentcore.application.port.out;

import org.flickit.flickitassessmentcore.domain.LevelCompetence;

import java.util.Set;

public interface LoadLevelCompetenceByMLPort {

    Set<LevelCompetence> loadLevelCompetenceByMLId(Long mlId);
}
