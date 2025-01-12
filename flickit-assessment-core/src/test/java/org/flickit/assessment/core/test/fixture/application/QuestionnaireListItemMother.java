package org.flickit.assessment.core.test.fixture.application;

import org.flickit.assessment.core.application.domain.QuestionnaireListItem;

import java.util.List;

public class QuestionnaireListItemMother {

    private static long id = 234L;

    public static QuestionnaireListItem createQuestionnaireListItem(int questionCount, int answerCount) {
        ++id;
        return new QuestionnaireListItem(
            id,
            "questionnaire",
            "description about questionnaire",
            1,
            questionCount,
            answerCount,
            10,
            (int) Math.floor(((double) answerCount / questionCount) * 100),
            List.of(
                new QuestionnaireListItem.Subject(0, "Team"),
                new QuestionnaireListItem.Subject(1, "Software")),
            null);
    }
}
