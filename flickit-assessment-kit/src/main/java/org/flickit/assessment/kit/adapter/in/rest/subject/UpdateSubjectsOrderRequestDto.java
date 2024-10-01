package org.flickit.assessment.kit.adapter.in.rest.subject;

import java.util.List;

public record UpdateSubjectsOrderRequestDto(List<Subject> subjects) {

    record Subject(Long id, Integer order){
    }
}
