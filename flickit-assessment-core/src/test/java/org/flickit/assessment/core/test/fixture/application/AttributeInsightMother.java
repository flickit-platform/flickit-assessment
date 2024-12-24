package org.flickit.assessment.core.test.fixture.application;

import org.flickit.assessment.core.application.domain.AttributeInsight;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;

public class AttributeInsightMother {

    public static AttributeInsight simpleAttributeAiInsight(){
        return new AttributeInsight(UUID.randomUUID(),
            new Random().nextLong(),
            "ai insight ",
            "assessor insight",
            LocalDateTime.now(),
            LocalDateTime.now(),
            "input path",
            false);
    }

    public static AttributeInsight simpleAttributeAiInsightMinInsightTime(){
        return new AttributeInsight(UUID.randomUUID(),
            new Random().nextLong(),
            "ai insight ",
            "assessor insight",
            LocalDateTime.MIN,
            LocalDateTime.now(),
            "input path",
            false);
    }
}
