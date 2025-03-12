package org.flickit.assessment.kit.test.fixture.application;

import org.flickit.assessment.kit.application.domain.Measure;
import org.flickit.assessment.kit.application.domain.Questionnaire;

import java.time.LocalDateTime;

public class MeasureMother {

    private static Long id = 431L;
    private static int index = 963;

    public static Measure measureFromQuestionnaire(Questionnaire questionnaire) {
        return new Measure(id++,
            questionnaire.getCode(),
            questionnaire.getTitle(),
            questionnaire.getIndex(),
            questionnaire.getDescription(),
            questionnaire.getCreationTime(),
            questionnaire.getLastModificationTime());
    }

    public static Measure measureWithTitle(String title) {
        return new Measure(
            id++,
            "c-" + title,
            title,
            index++,
            "Description",
            LocalDateTime.now(),
            LocalDateTime.now()
        );
    }
}
