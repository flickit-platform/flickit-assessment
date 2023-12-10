package org.flickit.assessment.kit.test.fixture.application;

import org.flickit.assessment.kit.application.domain.Questionnaire;

import java.time.LocalDateTime;

public class QuestionnaireMother {

    private static Long id = 134L;
    private static int index = 1;

    public static Questionnaire questionnaireWithTitle(String title) {
        return new Questionnaire(
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
