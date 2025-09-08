package org.flickit.assessment.core.adapter.in.rest.advicenarration;

import org.flickit.assessment.common.application.domain.advice.AdvicePlanItem;
import org.flickit.assessment.common.application.domain.advice.AttributeLevelTarget;

import java.util.List;

public record CreateAiAdviceNarrationRequestDto(List<AdvicePlanItem> adviceListItems,
                                                List<AttributeLevelTarget> attributeLevelTargets) {
}
