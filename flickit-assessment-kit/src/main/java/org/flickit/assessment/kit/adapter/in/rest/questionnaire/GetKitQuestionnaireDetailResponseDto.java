package org.flickit.assessment.kit.adapter.in.rest.questionnaire;

import org.flickit.assessment.common.application.domain.kit.KitLanguage;
import org.flickit.assessment.common.application.domain.kit.translation.QuestionnaireTranslation;

import java.util.List;
import java.util.Map;

public record GetKitQuestionnaireDetailResponseDto(
    int questionsCount,
    String description,
    List<Question> questions,
    Map<KitLanguage, QuestionnaireTranslation> translations
) {

    public record Question(
        long id,
        String title,
        int index,
        boolean mayNotBeApplicable,
        boolean advisable
    ) {}
}
