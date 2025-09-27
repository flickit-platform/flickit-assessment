package org.flickit.assessment.advice.test.fixture.application;

import org.flickit.assessment.advice.application.domain.AdviceNarration;

import java.time.LocalDateTime;
import java.util.UUID;

public class AdviceNarrationMother {

    public static AdviceNarration aiNarration() {
        return aiNarrationWithNarrationTime(LocalDateTime.now());
    }

    public static AdviceNarration aiNarrationWithNarrationTime(LocalDateTime narrationTime) {
        return new AdviceNarration(UUID.randomUUID(),
            UUID.randomUUID(),
            "aiNarration",
            null,
            false,
            narrationTime,
            null,
            null);
    }

    public static AdviceNarration assessorNarrationWithNarrationTime(LocalDateTime narrationTime) {
        return new AdviceNarration(UUID.randomUUID(),
            UUID.randomUUID(),
            "aiNarration",
            "assessorNarration",
            true,
            narrationTime,
            narrationTime,
            UUID.randomUUID());
    }
}
