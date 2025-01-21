package org.flickit.assessment.core.test.fixture.application;

import org.flickit.assessment.core.application.domain.AttributeInsight;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;

public class AttributeInsightMother {

    public static AttributeInsight simpleAttributeInsight() {
        return new AttributeInsight(UUID.randomUUID(),
            new Random().nextLong(),
            "ai insight ",
            "assessor insight",
            LocalDateTime.now().plusSeconds(10),
            LocalDateTime.now().plusSeconds(10),
            "input path",
            false,
            LocalDateTime.now().plusSeconds(10));
    }

    public static AttributeInsight simpleAttributeInsightMinInsightTime() {
        return new AttributeInsight(UUID.randomUUID(),
            new Random().nextLong(),
            "ai insight ",
            "assessor insight",
            LocalDateTime.MIN,
            LocalDateTime.now().plusSeconds(10),
            "input path",
            false,
            LocalDateTime.now().plusSeconds(10));
    }

    public static AttributeInsight simpleAttributeInsightMinInsightsTime() {
        return new AttributeInsight(UUID.randomUUID(),
            new Random().nextLong(),
            "ai insight ",
            "assessor insight",
            LocalDateTime.MIN,
            LocalDateTime.MIN,
            "input path",
            false,
            LocalDateTime.MIN);
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

    public static AttributeInsight attributeAiInsightWithTimes(LocalDateTime aiInsightTime, LocalDateTime lastModificationTime) {
        return new AttributeInsight(UUID.randomUUID(),
            new Random().nextLong(),
            "ai insight ",
            null,
            aiInsightTime,
            null,
            "input path",
            false,
            lastModificationTime);
    }
}
