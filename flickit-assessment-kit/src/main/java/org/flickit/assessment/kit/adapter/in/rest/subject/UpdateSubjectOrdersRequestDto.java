package org.flickit.assessment.kit.adapter.in.rest.subject;

import java.util.List;

public record UpdateSubjectOrdersRequestDto(List<SubjectOrderDto> orders) {

    record SubjectOrderDto(Long id, Integer index){
    }
}
