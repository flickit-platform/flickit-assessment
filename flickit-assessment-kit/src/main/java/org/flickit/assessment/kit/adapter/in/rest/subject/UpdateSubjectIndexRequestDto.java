package org.flickit.assessment.kit.adapter.in.rest.subject;

import java.util.List;

public record UpdateSubjectIndexRequestDto(List<subjectOrder> orders) {

    record subjectOrder(Long id, Integer order){

    }
}
