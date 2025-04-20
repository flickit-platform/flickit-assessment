package org.flickit.assessment.kit.adapter.in.rest.measure;

import org.flickit.assessment.common.application.domain.kit.translation.MeasureTranslation;

import java.util.Map;

public record UpdateMeasureRequestDto(Integer index,
                                      String title,
                                      String description,
                                      Map<String, MeasureTranslation> translations) {
}
