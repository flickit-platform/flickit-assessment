package org.flickit.assessment.kit.adapter.in.rest.attribute;

public record UpdateKitAttributeRequestDto(String code,
                                           String title,
                                           String description,
                                           Long subjectId,
                                           Integer index,
                                           Integer weight) {
}
