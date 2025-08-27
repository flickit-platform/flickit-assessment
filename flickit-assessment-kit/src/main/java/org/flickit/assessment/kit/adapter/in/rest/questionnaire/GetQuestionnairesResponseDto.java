package org.flickit.assessment.kit.adapter.in.rest.questionnaire;

import org.flickit.assessment.common.application.domain.kit.KitLanguage;
import org.flickit.assessment.common.application.domain.kit.translation.QuestionnaireTranslation;

import java.util.Map;

public record GetQuestionnairesResponseDto(long id,
                                           String title,
                                           int index,
                                           String description,
                                           Map<KitLanguage, QuestionnaireTranslation> translations,
                                           int questionsCount) {
}
