package org.flickit.assessment.kit.adapter.in.rest.answerrange;

import org.flickit.assessment.common.application.domain.kit.translation.AnswerOptionTranslation;

import java.util.Map;

public record CreateAnswerRangeOptionRequestDto(Long answerRangeId,
                                                Integer index,
                                                String title,
                                                Double value,
                                                Map<String, AnswerOptionTranslation> translations) {
}
