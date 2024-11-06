package org.flickit.assessment.kit.adapter.in.rest.answeroption;

public record CreateAnswerOptionRequestDto(Integer index,
                                           String title,
                                           Long answerRangeId,
                                           Double value) {
}
