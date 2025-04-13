package org.flickit.assessment.kit.test.fixture.application;

import org.flickit.assessment.common.application.domain.kit.KitLanguage;
import org.flickit.assessment.common.application.domain.kit.translation.SubjectTranslation;
import org.flickit.assessment.kit.application.domain.Attribute;
import org.flickit.assessment.kit.application.domain.Subject;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class SubjectMother {

    private static Long id = 134L;
    private static int index = 1;
    private static int weight = 1;

    public static Subject subjectWithTitle(String title) {
        return new Subject(
            id++,
            "c-" + title,
            title,
            index++,
            weight++,
            "Description",
            UUID.randomUUID(),
            UUID.randomUUID(),
            LocalDateTime.now(),
            LocalDateTime.now()
        );
    }

    public static Subject subjectWithAttributes(String title, List<Attribute> attributes) {
        return new Subject(
            id++,
            "c-" + title,
            title,
            index++,
            weight++,
            "Description",
            Map.of(KitLanguage.FA, new SubjectTranslation("translated title", "translated desc")),
            attributes,
            UUID.randomUUID(),
            UUID.randomUUID(),
            LocalDateTime.now(),
            LocalDateTime.now()
        );
    }

    public static Subject subjectWithTitleAndAttributes(String title, List<Attribute> attributes) {
        return new Subject(
            id++,
            "c-" + title,
            title,
            index++,
            weight++,
            "Description",
            Map.of(KitLanguage.FA, new SubjectTranslation("translated title", "translated desc")),
            attributes,
            UUID.randomUUID(),
            UUID.randomUUID(),
            LocalDateTime.now(),
            LocalDateTime.now()
        );
    }
}
