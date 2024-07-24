package org.flickit.assessment.core.test.fixture.application;

import org.flickit.assessment.core.application.domain.Questionnaire;

public class QuestionnaireMother {

    private static long id = 134L;

    public static Questionnaire createQuestionnaire() {
        return new Questionnaire(id++, "title" + id);
    }
}
