package org.flickit.flickitassessmentcore.application.port.out.levelcompetence;

import org.flickit.flickitassessmentcore.domain.LevelCompetence;

import java.util.List;

public interface LoadLevelCompetenceByMaturityLevelPort {

    Result loadByMaturityLevelId(Long maturityLevelId);

    record Result(List<LevelCompetence> levelCompetences) {}
}
