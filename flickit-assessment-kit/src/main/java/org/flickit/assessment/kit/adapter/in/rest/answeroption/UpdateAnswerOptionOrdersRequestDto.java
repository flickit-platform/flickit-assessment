package org.flickit.assessment.kit.adapter.in.rest.answeroption;

import java.util.List;

public record UpdateAnswerOptionOrdersRequestDto(List<MaturityLevelOrdersDto> orders) {

    record MaturityLevelOrdersDto(Long id, Integer index) {
    }
}
