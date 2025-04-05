package org.flickit.assessment.core.application.port.out.attributematurityscore;

import org.flickit.assessment.core.application.domain.MaturityLevel;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface LoadAttributeMaturityScoresPort {

    Map<Long, List<MaturityLevelScore>> loadAll(UUID assessmentResultId);

    record MaturityLevelScore(MaturityLevel maturityLevel, Double score) {
    }
}
