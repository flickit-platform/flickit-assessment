package org.flickit.assessment.kit.adapter.in.rest.attribute;

public record UpdateAttributeRequestDto(String title,
                                        String description,
                                        Long subjectId,
                                        Integer index,
                                        Integer weight) {
}
