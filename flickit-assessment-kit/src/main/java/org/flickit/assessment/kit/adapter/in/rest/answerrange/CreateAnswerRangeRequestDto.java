package org.flickit.assessment.kit.adapter.in.rest.answerrange;

import org.flickit.assessment.common.application.domain.kit.translation.AnswerRangeTranslation;

import java.util.Map;

public record CreateAnswerRangeRequestDto(String title,
                                          Map<String, AnswerRangeTranslation> translations) {
}
