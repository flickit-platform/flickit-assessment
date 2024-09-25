package org.flickit.assessment.advice.adapter.in.rest.advicenarration;

import org.flickit.assessment.advice.application.domain.AttributeLevelTarget;
import org.flickit.assessment.advice.application.domain.advice.AdviceListItem;

import java.util.List;

public record CreateAiAdviceNarrationRequestDto(List<AdviceListItem> adviceListItems,
                                                List<AttributeLevelTarget> attributeLevelTargets) {
}
