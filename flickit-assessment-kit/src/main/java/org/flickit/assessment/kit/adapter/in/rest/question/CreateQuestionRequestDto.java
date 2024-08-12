package org.flickit.assessment.kit.adapter.in.rest.question;

public record CreateQuestionRequestDto(
    Integer index,
    String title,
    String hint,
    Boolean mayNotBeApplicable,
    Boolean advisable) {}
