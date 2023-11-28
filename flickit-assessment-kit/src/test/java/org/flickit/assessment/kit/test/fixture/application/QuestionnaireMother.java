package org.flickit.assessment.kit.test.fixture.application;

import org.flickit.assessment.kit.application.domain.Questionnaire;

import java.time.LocalDateTime;

public class QuestionnaireMother {

    private static Long id = 134L;
    private static final String DESCRIPTION = "";

    public static Questionnaire questionnaire(String code, String title, int index) {
        id++;
        return new Questionnaire(
            id,
            code,
            title,
            index,
            DESCRIPTION,
            LocalDateTime.now(),
            LocalDateTime.now()
        );
    }
}
