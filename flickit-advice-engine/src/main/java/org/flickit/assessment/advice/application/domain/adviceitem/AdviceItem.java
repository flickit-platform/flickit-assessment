package org.flickit.assessment.advice.application.domain.adviceitem;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class AdviceItem {

    private final UUID id;
    private final String title;
    private final UUID assessmentResultId;
    private final String description;
    private final CostLevel cost;
    private final ImpactLevel impact;
    private final PriorityLevel priority;
}
