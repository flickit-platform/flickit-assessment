package org.flickit.assessment.core.test.fixture.application;

import org.flickit.assessment.core.application.domain.insight.SubjectInsight;

import java.time.LocalDateTime;
import java.util.UUID;

public class SubjectInsightMother {

    public static SubjectInsight defaultSubjectInsight(LocalDateTime insightTime, LocalDateTime insightLastModificationTime, boolean approved) {
        return new SubjectInsight(UUID.randomUUID(),
            2L,
            "insight",
            insightTime,
            insightLastModificationTime,
            null,
            approved);
    }

    public static SubjectInsight defaultSubjectInsight() {
        return new SubjectInsight(UUID.randomUUID(),
            2L,
            "insight",
            LocalDateTime.now(),
            LocalDateTime.now(),
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
            true);
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

    public static SubjectInsight subjectInsightWithTimesAndApproved(LocalDateTime insightTime,
                                                                    LocalDateTime lastModificationTIme,
                                                                    boolean approved) {
        return new SubjectInsight(UUID.randomUUID(),
            2L,
            "insight",
            insightTime,
            lastModificationTIme,
            UUID.randomUUID(),
            approved);
    }

    public static SubjectInsight subjectInsightWithSubjectId(Long subjectId) {
        return new SubjectInsight(UUID.randomUUID(),
            subjectId,
            "insight",
            LocalDateTime.now().plusSeconds(10),
            LocalDateTime.now().plusSeconds(10),
            UUID.randomUUID(),
            true);
    }
}
