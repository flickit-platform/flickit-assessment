package org.flickit.flickitassessmentcore.adapter.in.rest.assessmentcolor;
import org.flickit.flickitassessmentcore.application.port.in.assessment.GetAssessmentColorsUseCase.ColorItem;

import java.util.List;

public record ResponseDto(ColorItem defaultColor, List<ColorItem> colors) {
}
