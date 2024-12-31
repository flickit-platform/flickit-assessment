package org.flickit.assessment.core.test.fixture.application;

import org.flickit.assessment.core.application.domain.QuestionnaireListItem;

import java.util.List;

public class QuestionnaireListItemMother {

    static int id = 0;

    public static QuestionnaireListItem createWithoutIssues() {
        return new QuestionnaireListItem(
            id++,
            "questionnaire",
            "description about questionnaire",
            id++,
            1,
            0,
            1,
            0,
            List.of(new QuestionnaireListItem.Subject(0, "zero"),
                new QuestionnaireListItem.Subject(1, "one")),
            null);
    }
}
