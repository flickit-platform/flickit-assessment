package org.flickit.assessment.kit.adapter.in.rest.answerrange;

import org.flickit.assessment.common.application.domain.kit.translation.AnswerRangeTranslation;

import java.util.Map;

public record UpdateAnswerRangeRequestDto(String title,
                                          Boolean reusable,
                                          Map<String, AnswerRangeTranslation> translations) {}
