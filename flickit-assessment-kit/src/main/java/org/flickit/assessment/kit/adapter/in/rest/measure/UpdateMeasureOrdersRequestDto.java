package org.flickit.assessment.kit.adapter.in.rest.measure;

import java.util.List;

public record UpdateMeasureOrdersRequestDto(List<MeasureOrderDto> orders) {
    record MeasureOrderDto(Long id, Integer index) {
    }
}
