package org.flickit.assessment.kit.adapter.in.rest.attribute;

import java.util.List;

public record UpdateAttributeOrdersRequestDto(List<AttributeOrderDto> orders, Long subjectId) {

    record AttributeOrderDto(Long id, Integer index) {}
}
