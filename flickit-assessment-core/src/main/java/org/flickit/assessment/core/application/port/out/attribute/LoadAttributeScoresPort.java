package org.flickit.assessment.core.application.port.out.attribute;

import java.util.List;
import java.util.UUID;

public interface LoadAttributeScoresPort {

    List<Result> loadScores(UUID assessmentId, long attributeId, long maturityLevelId);

    record Result(long questionId,
                  int questionWeight,
                  Double answerScore,
                  Boolean answerIsNotApplicable) {
    }
}
