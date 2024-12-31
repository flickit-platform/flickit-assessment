package org.flickit.assessment.core.test.fixture.application;

import org.flickit.assessment.core.application.domain.QuestionnaireListItem;

import java.util.List;

public class QuestionnaireListItemMother {

    public static QuestionnaireListItem createWithoutIssues() {
        return new QuestionnaireListItem(
            0,
            "questionnaire",
            "description about questionnaire",
            1,
            1,
            0,
            1,
            0,
            List.of(new QuestionnaireListItem.Subject(0, "zero"),
                new QuestionnaireListItem.Subject(1, "one")),
            null);
    }
}
