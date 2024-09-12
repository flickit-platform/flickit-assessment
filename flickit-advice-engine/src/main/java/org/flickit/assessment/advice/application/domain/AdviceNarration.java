package org.flickit.assessment.advice.application.domain;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class AdviceNarration {

    private final UUID id;
    private final UUID assessmentResultId;
    private final String aiNarration;
    private final String assessorNarration;
    private final LocalDateTime aiNarrationTime;
    private final LocalDateTime assessorNarrationTime;
    private final UUID createdBy;
}
