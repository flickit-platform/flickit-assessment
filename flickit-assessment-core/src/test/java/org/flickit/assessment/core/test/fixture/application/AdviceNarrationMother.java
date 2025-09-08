package org.flickit.assessment.core.test.fixture.application;

import org.flickit.assessment.core.application.domain.AdviceNarration;

import java.time.LocalDateTime;
import java.util.UUID;

public class AdviceNarrationMother {

    public static AdviceNarration aiNarration() {
        return aiAdviceNarrationWithTime(LocalDateTime.now());
    }

    public static AdviceNarration aiAdviceNarrationWithTime(LocalDateTime narrationTime) {
        return new AdviceNarration(UUID.randomUUID(),
            "aiNarration",
            null,
            false,
            narrationTime,
            null,
            narrationTime,
            null);
    }

    public static AdviceNarration assessorAdviceNarrationWithTime(LocalDateTime narrationTime) {
        return new AdviceNarration(UUID.randomUUID(),
            "aiNarration",
            "assessorNarration",
            true,
            LocalDateTime.MIN,
            narrationTime,
            narrationTime,
            UUID.randomUUID());
    }
}
