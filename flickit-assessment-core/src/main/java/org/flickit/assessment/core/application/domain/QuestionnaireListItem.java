package org.flickit.assessment.core.application.domain;

import java.util.List;

public record QuestionnaireListItem(
    long id,
    String title,
    String description,
    int index,
    int questionCount,
    int answerCount,
    int nextQuestion,
    int progress,
    List<Subject> subjects) {

    public record Subject(
        long id,
        String title) {
    }
}
