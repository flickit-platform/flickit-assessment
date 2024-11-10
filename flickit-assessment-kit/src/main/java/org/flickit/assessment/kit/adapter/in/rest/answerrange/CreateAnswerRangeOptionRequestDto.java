package org.flickit.assessment.kit.adapter.in.rest.answerrange;

public record CreateAnswerRangeOptionRequestDto(Long answerRangeId,
                                                Integer index,
                                                String title,
                                                Double value) {
}
