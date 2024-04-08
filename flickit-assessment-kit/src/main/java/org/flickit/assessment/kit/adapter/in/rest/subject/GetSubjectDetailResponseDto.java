package org.flickit.assessment.kit.adapter.in.rest.subject;

import org.flickit.assessment.kit.application.port.in.subject.GetSubjectDetailUseCase.Attribute;

import java.util.List;

public record GetSubjectDetailResponseDto(Integer questionCount,
                                          String description,
                                          List<Attribute> attributes) {
}
