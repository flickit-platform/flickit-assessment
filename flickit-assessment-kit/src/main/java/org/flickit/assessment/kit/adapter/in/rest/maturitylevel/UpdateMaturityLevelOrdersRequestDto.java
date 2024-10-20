package org.flickit.assessment.kit.adapter.in.rest.maturitylevel;

import java.util.List;

public record UpdateMaturityLevelOrdersRequestDto(List<MaturityLevelOrderDto> orders) {

    record MaturityLevelOrderDto(Long id, Integer index) {
    }
}
