package org.flickit.assessment.kit.adapter.in.rest.questionnaire;

import org.flickit.assessment.common.application.domain.kit.translation.QuestionnaireTranslation;

import java.util.Map;

public record CreateQuestionnaireRequestDto(Integer index,
                                            String title,
                                            String description,
                                            Map<String, QuestionnaireTranslation> translations) {
}
