package org.flickit.assessment.core.application.domain;

import lombok.With;

import java.util.List;

public record QuestionnaireListItem(long id,
                                    String title,
                                    String description,
                                    int index,
                                    int questionCount,
                                    int answerCount,
                                    int nextQuestion,
                                    int progress,
                                    List<Subject> subjects,
                                    @With Issues issues) {

    public record Subject(long id, String title) {
    }

    public record Issues(int unanswered,
                         int answeredWithLowConfidence,
                         int answeredWithoutEvidence,
                         int unresolvedComments) {
    }
}
