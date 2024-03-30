package org.flickit.assessment.kit.application.domain;

import java.util.List;

public record QuestionnaireListItem(
    long id,
    String title,
    int index,
    int questionCount,
    int answersCount,
    int progress,
    List<Subject> subjects) {

    public record Subject(
        long id,
        String title) {
    }
}
