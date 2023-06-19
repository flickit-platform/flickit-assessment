package org.flickit.assessment.core.application.port.out.levelcompetence;

import org.flickit.assessment.core.domain.LevelCompetence;

import java.util.Set;

public interface LoadLevelCompetenceByMaturityLevelPort {

    Set<LevelCompetence> loadLevelCompetenceByMaturityLevelId(Long mlId);
}
