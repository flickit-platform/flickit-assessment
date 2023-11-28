package org.flickit.assessment.kit.test.fixture.application;

import org.flickit.assessment.kit.application.domain.Attribute;

import java.time.LocalDateTime;

public class AttributeMother {

    private static Long id = 234L;

    public static Attribute createAttribute(String code, String title, int index, String description, int weight) {
        return new Attribute(
            id++,
            code,
            title,
            index,
            description,
            weight,
            LocalDateTime.now(),
            LocalDateTime.now()
        );
    }
}
