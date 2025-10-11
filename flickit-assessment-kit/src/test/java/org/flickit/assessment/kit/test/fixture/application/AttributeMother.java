package org.flickit.assessment.kit.test.fixture.application;

import org.flickit.assessment.common.application.domain.kit.KitLanguage;
import org.flickit.assessment.common.application.domain.kit.translation.AttributeTranslation;
import org.flickit.assessment.kit.application.domain.Attribute;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

public class AttributeMother {

    private static long id = 1013;
    private static int index = 1;

    public static Attribute attributeWithTitle(String title) {
        var attribute = new Attribute(
            id++,
            "c-" + title,
            title,
            index++,
            "Description",
            1,
            LocalDateTime.now(),
            LocalDateTime.now(),
            UUID.randomUUID(),
            UUID.randomUUID()
        );
        attribute.setTranslations(Map.of(KitLanguage.FA, new AttributeTranslation("title", "description")));
        return attribute;
    }

    public static Attribute createAttribute(String code, String title, int index, String description, int weight) {
        return new Attribute(
            id++,
            code,
            title,
            index,
            description,
            weight,
            LocalDateTime.now(),
            LocalDateTime.now(),
            UUID.randomUUID(),
            UUID.randomUUID()
        );
    }
}
