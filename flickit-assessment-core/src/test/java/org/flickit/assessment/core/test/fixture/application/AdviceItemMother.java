package org.flickit.assessment.core.test.fixture.application;

import org.flickit.assessment.core.application.domain.AdviceItem;

import java.util.UUID;

public class AdviceItemMother {

    public static AdviceItem adviceItem() {
        return new AdviceItem(
            UUID.randomUUID(),
            "title",
            "desc",
            "Low",
            "High",
            "Medium");
    }
}
