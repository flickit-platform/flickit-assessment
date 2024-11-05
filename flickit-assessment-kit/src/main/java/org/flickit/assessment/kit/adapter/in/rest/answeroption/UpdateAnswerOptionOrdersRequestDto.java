package org.flickit.assessment.kit.adapter.in.rest.answeroption;

import java.util.List;

public record UpdateAnswerOptionOrdersRequestDto(List<AnswerOptionOrdersDto> orders) {

    record AnswerOptionOrdersDto(Long id, Integer index) {
    }
}
