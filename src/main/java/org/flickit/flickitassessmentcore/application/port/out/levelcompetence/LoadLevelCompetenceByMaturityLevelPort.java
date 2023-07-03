package org.flickit.flickitassessmentcore.application.port.out.levelcompetence;

import org.flickit.flickitassessmentcore.domain.LevelCompetence;

import java.util.Set;

public interface LoadLevelCompetenceByMaturityLevelPort {

    Result loadLevelCompetenceByMaturityLevelId(Param param);

    record Param(Long maturityLevelId) {}

    record Result(Set<LevelCompetence> levelCompetences) {}
}
