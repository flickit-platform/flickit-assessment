package org.flickit.assessment.kit.adapter.in.rest.answeroption;

import org.flickit.assessment.common.application.domain.kit.translation.AnswerOptionTranslation;

import java.util.Map;

public record UpdateAnswerOptionRequestDto(Integer index,
                                           String title,
                                           Double value,
                                           Map<String, AnswerOptionTranslation> translations) {
}
