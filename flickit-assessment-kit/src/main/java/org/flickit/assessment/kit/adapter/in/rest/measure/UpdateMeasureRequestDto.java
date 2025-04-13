package org.flickit.assessment.kit.adapter.in.rest.measure;

public record UpdateMeasureRequestDto(Integer index,
                                      String title,
                                      String description) {
}
