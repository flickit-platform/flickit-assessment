package org.flickit.assessment.kit.adapter.in.rest.subject;

import java.util.List;

public record UpdateSubjectOrdersRequestDto(List<Subject> subjects) {

    record Subject(Long id, Integer index){
    }
}
