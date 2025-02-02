package org.flickit.assessment.core.test.fixture.application;

import org.flickit.assessment.core.application.domain.SubjectInsight;

import java.time.LocalDateTime;
import java.util.UUID;

public class SubjectInsightMother {

    public static SubjectInsight approvedSubjectInsight() {
        return new SubjectInsight(UUID.randomUUID(),
            2L,
            "insight",
            LocalDateTime.now().plusSeconds(10),
            LocalDateTime.now().plusSeconds(10),
            UUID.randomUUID(),
            true);
    }

    public static SubjectInsight defaultSubjectInsight() {
        return new SubjectInsight(UUID.randomUUID(),
            2L,
            "insight",
            LocalDateTime.now().plusSeconds(10),
            LocalDateTime.now().plusSeconds(10),
            null,
            false);
    }

    public static SubjectInsight subjectInsight() {
        return new SubjectInsight(UUID.randomUUID(),
            2L,
            "insight",
            LocalDateTime.now().plusSeconds(10),
            LocalDateTime.now().plusSeconds(10),
            UUID.randomUUID(),
            false);
    }

    public static SubjectInsight subjectInsightMinInsightTime() {
        return new SubjectInsight(UUID.randomUUID(),
            2L,
            "insight",
            LocalDateTime.MIN,
            LocalDateTime.MIN,
            UUID.randomUUID(),
            false);
    }
}
