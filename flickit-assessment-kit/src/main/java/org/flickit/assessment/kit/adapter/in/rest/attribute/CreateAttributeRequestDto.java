package org.flickit.assessment.kit.adapter.in.rest.attribute;

public record CreateAttributeRequestDto(Integer index,
                                        String title,
                                        String description,
                                        Integer weight,
                                        Long subjectId) {
}
