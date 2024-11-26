package org.flickit.assessment.advice.application.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.flickit.assessment.common.application.domain.ID;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class AdviceNarration {

    private final ID id;
    private final ID assessmentResultId;
    private final String aiNarration;
    private final String assessorNarration;
    private final LocalDateTime aiNarrationTime;
    private final LocalDateTime assessorNarrationTime;
    private final ID createdBy;
}
