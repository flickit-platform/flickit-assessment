package org.flickit.assessment.kit.adapter.in.rest.answeroption;

public record CreateAnswerOptionRequestDto(Long questionId,
                                           Integer index,
                                           String title,
                                           Double value) {
}
