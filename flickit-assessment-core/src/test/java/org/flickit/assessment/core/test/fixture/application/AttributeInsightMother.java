package org.flickit.assessment.core.test.fixture.application;

import org.flickit.assessment.core.application.domain.AttributeInsight;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;

public class AttributeInsightMother {

    public static AttributeInsight insightWithTimeAndApproved(LocalDateTime insightTime, boolean approved) {
        return new AttributeInsight(UUID.randomUUID(),
            new Random().nextLong(),
            "ai insight ",
            "assessor insight",
            insightTime,
            insightTime,
            "input path",
            approved,
            insightTime);
    }

    public static AttributeInsight attributeInsightWithTimes(LocalDateTime aiInsightTime, LocalDateTime assessorInsightTime, LocalDateTime lastModificationTime) {
        return new AttributeInsight(UUID.randomUUID(),
            new Random().nextLong(),
            "ai insight ",
            "assessor insight",
            aiInsightTime,
            assessorInsightTime,
            "input path",
            true,
            lastModificationTime);
    }

    public static AttributeInsight aiInsightWithTime(LocalDateTime insightTime) {
        return new AttributeInsight(UUID.randomUUID(),
            new Random().nextLong(),
            "ai insight ",
            null,
            insightTime,
            null,
            "input path",
            false,
            insightTime);
    }
}
