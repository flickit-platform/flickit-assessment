package org.flickit.assessment.kit.application.port.out.levelcomptenece;

import java.util.Map;

public interface LoadLevelCompetenceAsMapByMaturityLevelPort {

    Map<String,Integer> loadByMaturityLevelId(Long maturityLevelId);
}
