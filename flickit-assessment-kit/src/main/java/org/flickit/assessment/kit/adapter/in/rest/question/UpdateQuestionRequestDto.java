package org.flickit.assessment.kit.adapter.in.rest.question;

import org.flickit.assessment.common.application.domain.kit.translation.QuestionTranslation;

import java.util.Map;

public record UpdateQuestionRequestDto(Integer index,
                                       String title,
                                       String hint,
                                       Boolean mayNotBeApplicable,
                                       Boolean advisable,
                                       Long answerRangeId,
                                       Map<String, QuestionTranslation> translations) {
}
