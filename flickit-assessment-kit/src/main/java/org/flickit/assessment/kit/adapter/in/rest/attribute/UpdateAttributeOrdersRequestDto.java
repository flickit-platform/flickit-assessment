package org.flickit.assessment.kit.adapter.in.rest.attribute;

import java.util.List;

public record UpdateAttributeOrdersRequestDto(List<AttributeOrderDto> orders) {

    record AttributeOrderDto(Long id, Integer index) {
    }
}
