package org.flickit.assessment.common.application.module.adviceengine;

import lombok.Builder;
import org.flickit.assessment.common.application.domain.advice.AdvicePlanItem;
import org.flickit.assessment.common.application.domain.advice.AttributeLevelTarget;

import java.util.List;
import java.util.UUID;

public interface GenerateAdvicePlanInternalApi {

    Result generate(Param param);

    @Builder
    record Param(UUID assessmentId, List<AttributeLevelTarget> attributeLevelTargets) {
    }

    @Builder
    record Result(List<AdvicePlanItem> advicePlanItems) {
    }
}
