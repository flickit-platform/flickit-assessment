package org.flickit.assessment.core.test.fixture.application;

import org.flickit.assessment.core.application.domain.QuestionnaireListItem;

import java.util.List;

public class QuestionnaireListItemMother {

    private static int id = 0;

    public static QuestionnaireListItem createWithoutIssues() {
        ++id;
        return new QuestionnaireListItem(
            id,
            "questionnaire",
            "description about questionnaire",
            id,
            10,
            10,
            10,
            100,
            List.of(new QuestionnaireListItem.Subject(0, "zero"),
                new QuestionnaireListItem.Subject(1, "one")),
            new QuestionnaireListItem.Issues(0, 0, 0, 0));
    }
}