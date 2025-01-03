package org.flickit.assessment.core.test.fixture.application;

import org.flickit.assessment.core.application.domain.QuestionnaireListItem;

import java.util.List;

public class QuestionnaireListItemMother {

    private static int id = 0;

    public static QuestionnaireListItem createWithoutIssuesByQuestionCountAndAnswerCount(int questionCount, int answerCount) {
        ++id;
        return new QuestionnaireListItem(
            id,
            "questionnaire",
            "description about questionnaire",
            id,
            questionCount,
            answerCount,
            10,
            answerCount / questionCount,
            List.of(new QuestionnaireListItem.Subject(0, "zero"),
                new QuestionnaireListItem.Subject(1, "one")),
            new QuestionnaireListItem.Issues(0, 0, 0, 0));
    }

    public static QuestionnaireListItem createContainingIssuesByQuestionCountAndAnswerCount(int questionCount, int answerCount) {
        ++id;
        return new QuestionnaireListItem(
            id,
            "questionnaire",
            "description about questionnaire",
            id,
            questionCount,
            answerCount,
            answerCount + 1,
            answerCount / questionCount,
            List.of(new QuestionnaireListItem.Subject(0, "zero"),
                new QuestionnaireListItem.Subject(1, "one")),
            new QuestionnaireListItem.Issues(1, 2, 3, 4));
    }
}