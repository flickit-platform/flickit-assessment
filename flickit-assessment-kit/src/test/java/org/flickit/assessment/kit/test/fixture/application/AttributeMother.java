package org.flickit.assessment.kit.test.fixture.application;

import org.flickit.assessment.kit.application.domain.Attribute;

import java.time.LocalDateTime;

public class AttributeMother {

    private static long id = 1013;
    private static int index = 1;

    public static Attribute attributeWithTitle(String title) {
        return new Attribute(
            id++,
            "c-" + title,
            title,
            index++,
            "Description",
            1,
            LocalDateTime.now(),
            LocalDateTime.now()
        );
    }
}
