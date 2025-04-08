package org.flickit.assessment.kit.adapter.in.rest.attribute;

import org.flickit.assessment.common.application.domain.kit.translate.AttributeTranslation;

import java.util.Map;

public record UpdateAttributeRequestDto(Integer index,
                                        String title,
                                        String description,
                                        Integer weight,
                                        Long subjectId,
                                        Map<String, AttributeTranslation> translations) {
}
