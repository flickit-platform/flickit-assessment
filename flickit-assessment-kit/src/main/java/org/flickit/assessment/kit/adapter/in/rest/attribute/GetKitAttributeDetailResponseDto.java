package org.flickit.assessment.kit.adapter.in.rest.attribute;

import org.flickit.assessment.common.application.domain.kit.KitLanguage;
import org.flickit.assessment.common.application.domain.kit.translation.AttributeTranslation;
import org.flickit.assessment.kit.application.port.in.attribute.GetKitAttributeDetailUseCase;

import java.util.List;
import java.util.Map;

public record GetKitAttributeDetailResponseDto(Long id,
                                               Integer index,
                                               String title,
                                               Integer questionCount,
                                               Integer weight,
                                               String description,
                                               List<GetKitAttributeDetailUseCase.MaturityLevel> maturityLevels,
                                               Map<KitLanguage, AttributeTranslation> translations) {
}
