package org.flickit.assessment.core.test.fixture.application;

import org.flickit.assessment.core.application.domain.SubjectInsight;

import java.time.LocalDateTime;
import java.util.UUID;

public class SubjectInsightMother {

    public static SubjectInsight subjectInsight() {
        return new SubjectInsight(UUID.randomUUID(),
            2L,
            "insight", LocalDateTime.now().plusSeconds(10),
            UUID.randomUUID());
    }

    public static SubjectInsight subjectInsightMinInsightTime() {
        return new SubjectInsight(UUID.randomUUID(),
            2L,
            "insight", LocalDateTime.MIN,
            UUID.randomUUID());
    }
}
