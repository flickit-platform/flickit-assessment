package org.flickit.assessment.core.adapter.in.rest.assessmentcolor;
import org.flickit.assessment.core.application.port.in.assessment.GetAssessmentColorsUseCase.ColorItem;

import java.util.List;

public record GetAssessmentColorsResponseDto(ColorItem defaultColor, List<ColorItem> colors) {
}
