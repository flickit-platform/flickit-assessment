package org.flickit.assessment.kit.adapter.in.rest.subject;

import java.util.List;

public record UpdateSubjectsOrderRequestDto(List<SubjectOrder> orders) {

    record SubjectOrder(Long id, Integer order){

    }
}
