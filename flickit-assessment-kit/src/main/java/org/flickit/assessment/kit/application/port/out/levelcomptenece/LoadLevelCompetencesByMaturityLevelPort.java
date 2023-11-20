package org.flickit.assessment.kit.application.port.out.levelcomptenece;

import org.flickit.assessment.kit.application.domain.MaturityLevelCompetence;

import java.util.List;

public interface LoadLevelCompetencesByMaturityLevelPort {

    List<MaturityLevelCompetence> loadByMaturityLevelId(Long maturityLevelId);
}
