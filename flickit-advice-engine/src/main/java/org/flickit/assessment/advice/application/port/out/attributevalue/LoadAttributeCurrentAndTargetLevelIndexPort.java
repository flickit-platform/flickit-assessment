package org.flickit.assessment.advice.application.port.out.attributevalue;

import org.flickit.assessment.advice.application.domain.AttributeLevelTarget;

import java.util.List;
import java.util.UUID;

public interface LoadAttributeCurrentAndTargetLevelIndexPort {

    /**
     * Loads the current and target maturity level indices for attributes that are associated with
     * the given assessment and listed in {@code attributeLevelTargets}.
     *
     * @param assessmentId          The unique identifier of the assessment.
     * @param attributeLevelTargets A list of attribute level targets containing information about the desired maturity levels.
     * @return A list of {@link Result} containing the attribute ID, current maturity level index, and target maturity level index.
     */
    List<Result> load(UUID assessmentId, List<AttributeLevelTarget> attributeLevelTargets);

    record Result(
        long attributeId,
        int currentMaturityLevelIndex,
        int targetMaturityLevelIndex) {
    }
}
