package org.flickit.assessment.kit.adapter.in.rest.measure;

import org.flickit.assessment.common.application.domain.kit.KitLanguage;
import org.flickit.assessment.common.application.domain.kit.translation.MeasureTranslation;

import java.util.Map;

public record GetMeasuresResponseDto(long id,
                                     String title,
                                     int index,
                                     String description,
                                     Map<KitLanguage, MeasureTranslation> translations,
                                     int questionsCount) {
}
