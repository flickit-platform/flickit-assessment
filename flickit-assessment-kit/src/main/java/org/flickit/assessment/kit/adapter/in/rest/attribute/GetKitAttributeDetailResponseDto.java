package org.flickit.assessment.kit.adapter.in.rest.attribute;

import org.flickit.assessment.kit.application.port.in.attribute.GetKitAttributeDetailUseCase;

import java.util.List;

public record GetKitAttributeDetailResponseDto(Long id,
                                               Integer index,
                                               String title,
                                               Integer questionCount,
                                               Integer weight,
                                               String description,
                                               List<GetKitAttributeDetailUseCase.MaturityLevel> maturityLevels) {
}
