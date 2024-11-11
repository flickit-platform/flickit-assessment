package org.flickit.assessment.kit.adapter.in.rest.question;

public record UpdateQuestionRequestDto(Integer index,
                                       String title,
                                       String hint,
                                       Boolean mayNotBeApplicable,
                                       Long answerRangeId,
                                       Boolean advisable) {
}
