package org.flickit.assessment.kit.adapter.in.rest.answerrange;

public record CreateReusableAnswerOptionRequestDto(Long answerRangeId,
                                                   Integer index,
                                                   String title,
                                                   Double value) {
}
