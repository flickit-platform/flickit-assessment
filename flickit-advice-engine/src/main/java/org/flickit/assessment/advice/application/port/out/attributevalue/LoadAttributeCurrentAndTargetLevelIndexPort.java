package org.flickit.assessment.advice.application.port.out.attributevalue;

import org.flickit.assessment.advice.application.domain.AttributeLevelTarget;

import java.util.List;
import java.util.UUID;

public interface LoadAttributeCurrentAndTargetLevelIndexPort {

    List<Result> loadAttributeCurrentAndTargetLevelIndex(UUID assessmentId, List<AttributeLevelTarget> attributeLevelTargets);

    record Result(
        long attributeId,
        int currentMaturityLevelIndex,
        int targetMaturityLevelIndex) {
    }
}
