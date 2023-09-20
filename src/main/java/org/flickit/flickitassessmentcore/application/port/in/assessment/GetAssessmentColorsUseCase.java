package org.flickit.flickitassessmentcore.application.port.in.assessment;

import java.util.List;

public interface GetAssessmentColorsUseCase {

    AssessmentColors getAssessmentColors();

    record AssessmentColors(ColorItem defaultColor, List<ColorItem> colors) {}

    record ColorItem(int id, String title, String code) {}
}

