package org.flickit.assessment.advice.application.port.out.attributevalue;

import org.flickit.assessment.advice.application.port.in.CreateAdviceUseCase;

import java.util.List;
import java.util.UUID;

public interface LoadAttributeCurrentAndTargetLevelIndexPort {

    List<Result> loadAttributeCurrentAndTargetLevelIndex(UUID assessmentId, List<CreateAdviceUseCase.AttributeLevelTarget> attributeLevelTargets);

    record Result(
        long attributeId,
        int currentMaturityLevelIndex,
        int targetMaturityLevelIndex
    ) {
    }
}
