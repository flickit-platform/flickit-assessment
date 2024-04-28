package org.flickit.assessment.kit.adapter.in.rest.subject;

import org.flickit.assessment.kit.application.port.in.subject.GetKitSubjectDetailUseCase.Attribute;

import java.util.List;

public record GetKitSubjectDetailResponseDto(int questionsCount,
                                             String description,
                                             List<Attribute> attributes) {
}
