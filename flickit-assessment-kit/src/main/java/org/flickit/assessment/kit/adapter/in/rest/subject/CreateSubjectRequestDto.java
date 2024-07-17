package org.flickit.assessment.kit.adapter.in.rest.subject;

public record CreateSubjectRequestDto(
    Integer index,
    String title,
    String description,
    Integer weight,
    Long expertGroupId) {}
