package org.flickit.assessment.advice.test.fixture.application;

import org.flickit.assessment.advice.application.domain.AdviceNarration;

import java.time.LocalDateTime;
import java.util.UUID;

public class AdviceNarrationMother {

    public static AdviceNarration aiNarration() {
        return new AdviceNarration(UUID.randomUUID(),
            UUID.randomUUID(),
            "aiNarration",
            null,
            LocalDateTime.now(),
            null,
            null);
    }

    public static AdviceNarration assessorNarration() {
        return new AdviceNarration(UUID.randomUUID(),
            UUID.randomUUID(),
            "aiNarration",
            "assessorNarration",
            LocalDateTime.now(),
            LocalDateTime.now(),
            UUID.randomUUID());
    }
}
