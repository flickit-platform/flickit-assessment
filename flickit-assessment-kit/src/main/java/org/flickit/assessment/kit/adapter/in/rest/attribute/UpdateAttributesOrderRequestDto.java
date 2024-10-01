package org.flickit.assessment.kit.adapter.in.rest.attribute;

import java.util.List;

public record UpdateAttributesOrderRequestDto(List<Attribute> attributes) {

    record Attribute(Long id, Integer index){
    }
}
