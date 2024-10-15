package org.flickit.assessment.kit.adapter.in.rest.attribute;

public record UpdateAttributeRequestDto(Integer index,
                                        String title,
                                        String description,
                                        Integer weight,
                                        Long subjectId
) {
}
