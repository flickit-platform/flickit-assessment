package org.flickit.assessment.kit.adapter.in.rest.questionnaire;

import java.util.List;

public record GetKitQuestionnaireDetailResponseDto(
    int questionsCount,
    List<String> relatedSubjects,
    String description,
    List<Question> questions
) {

    public record Question(
        long id,
        String title,
        int index,
        boolean mayNotBeApplicable,
        boolean advisable
    ) {}
}
