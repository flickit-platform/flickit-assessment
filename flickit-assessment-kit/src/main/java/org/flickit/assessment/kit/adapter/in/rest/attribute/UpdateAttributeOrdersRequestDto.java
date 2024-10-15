package org.flickit.assessment.kit.adapter.in.rest.attribute;

import java.util.List;

public record UpdateAttributeOrdersRequestDto(List<Attribute> attributes) {

    record Attribute(Long id, Integer index){
    }
}
