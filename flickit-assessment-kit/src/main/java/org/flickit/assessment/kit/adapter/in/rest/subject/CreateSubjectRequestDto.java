package org.flickit.assessment.kit.adapter.in.rest.subject;

public record CreateSubjectRequestDto(
    Integer index,
    String title,
    String description,
    Integer weight) {

    public CreateSubjectRequestDto {
        if (weight == null)
            weight = 1;
    }
}
