package org.flickit.assessment.core.test.fixture.application;

import org.flickit.assessment.core.application.domain.AdviceNarration;

import java.time.LocalDateTime;
import java.util.UUID;

public class AdviceNarrationMother {

    public static AdviceNarration aiAdviceNarrationWithTime(LocalDateTime narrationTime) {
        return new AdviceNarration(UUID.randomUUID(),
            false,
            narrationTime,
            null,
            null);
    }

    public static AdviceNarration assessorAdviceNarrationWithTime(LocalDateTime narrationTime) {
        return new AdviceNarration(UUID.randomUUID(),
            true,
            LocalDateTime.MIN,
            narrationTime,
            UUID.randomUUID());
    }
}
