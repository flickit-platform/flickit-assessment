package org.flickit.flickitassessmentcore.application.port.out.levelcompetence;

import org.flickit.flickitassessmentcore.domain.LevelCompetence;

import java.util.Set;

public interface LoadLevelCompetenceByMaturityLevelPort {

    Set<LevelCompetence> loadLevelCompetenceByMaturityLevelId(Long mlId);
}
