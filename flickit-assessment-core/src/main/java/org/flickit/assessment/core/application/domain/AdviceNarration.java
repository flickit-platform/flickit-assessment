package org.flickit.assessment.core.application.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class AdviceNarration {

    private final UUID id;
    private final String aiNarration;
    private final String assessorNarration;
    private final boolean approved;
    private final LocalDateTime aiNarrationTime;
    private final LocalDateTime assessorNarrationTime;
    private final UUID createdBy;
}
