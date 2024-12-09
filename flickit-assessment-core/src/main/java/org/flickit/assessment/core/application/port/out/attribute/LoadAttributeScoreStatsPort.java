package org.flickit.assessment.core.application.port.out.attribute;

import java.util.List;
import java.util.UUID;

public interface LoadAttributeScoreStatsPort {

    List<Result> loadDetails(UUID assessmentId, long attributeId, long maturityLevelId);

    record Result(Long questionId,
                  Double questionWeight,
                  Double answerScore,
                  Boolean answerIsNotApplicable) {
    }
}
