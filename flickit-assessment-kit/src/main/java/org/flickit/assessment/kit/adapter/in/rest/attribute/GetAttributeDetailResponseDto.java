package org.flickit.assessment.kit.adapter.in.rest.attribute;

import org.flickit.assessment.kit.application.port.in.attribute.GetAttributeDetailUseCase;

import java.util.List;

public record GetAttributeDetailResponseDto(Long id,
                                            Integer index,
                                            String title,
                                            Integer questionCount,
                                            Integer weight,
                                            String description,
                                            List<GetAttributeDetailUseCase.MaturityLevel> maturityLevels) {
}
