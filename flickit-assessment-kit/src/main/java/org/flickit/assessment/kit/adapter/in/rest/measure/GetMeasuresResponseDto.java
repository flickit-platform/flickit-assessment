package org.flickit.assessment.kit.adapter.in.rest.measure;

public record GetMeasuresResponseDto(long id,
                                     String title,
                                     int index,
                                     String description,
                                     int questionsCount) {
}
