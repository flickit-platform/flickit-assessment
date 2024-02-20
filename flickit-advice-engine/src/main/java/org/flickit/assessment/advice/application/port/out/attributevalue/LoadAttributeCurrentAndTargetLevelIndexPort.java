package org.flickit.assessment.advice.application.port.out.attributevalue;

import org.flickit.assessment.advice.application.port.in.CreateAdviceUseCase;

import java.util.List;
import java.util.UUID;

public interface LoadAttributeCurrentAndTargetLevelIndexPort {

    List<Result> loadAttributeCurrentAndTargetLevelIndex(List<CreateAdviceUseCase.AttributeLevelTarget> attributeLevelTargets, UUID assessmentId);

    record Result(
        long attributeId,
        int currentMaturityLevelIndex,
        int targetMaturityLevelIndex
    ) {
    }
}
