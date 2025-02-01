package org.flickit.assessment.advice.test.fixture.application;

import org.flickit.assessment.advice.application.domain.AdviceItem;
import org.flickit.assessment.common.application.domain.adviceitem.CostLevel;
import org.flickit.assessment.common.application.domain.adviceitem.ImpactLevel;
import org.flickit.assessment.common.application.domain.adviceitem.PriorityLevel;

import java.time.LocalDateTime;
import java.util.UUID;

public class AdviceItemMother {

    public static AdviceItem adviceItem() {
        return new AdviceItem(
            UUID.randomUUID(),
            "title",
            UUID.randomUUID(),
            "description",
            CostLevel.MEDIUM,
            PriorityLevel.HIGH,
            ImpactLevel.LOW,
            LocalDateTime.now(),
            LocalDateTime.now(),
            UUID.randomUUID(),
            UUID.randomUUID()
        );
    }
}
