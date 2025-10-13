package org.flickit.assessment.kit.adapter.in.rest.subject;

import org.flickit.assessment.common.application.domain.kit.KitLanguage;
import org.flickit.assessment.common.application.domain.kit.translation.SubjectTranslation;
import org.flickit.assessment.kit.application.port.in.subject.GetKitSubjectDetailUseCase.Attribute;

import java.util.List;
import java.util.Map;

public record GetKitSubjectDetailResponseDto(int questionsCount,
                                             String description,
                                             int weight,
                                             List<Attribute> attributes,
                                             Map<KitLanguage, SubjectTranslation> translations) {
}
