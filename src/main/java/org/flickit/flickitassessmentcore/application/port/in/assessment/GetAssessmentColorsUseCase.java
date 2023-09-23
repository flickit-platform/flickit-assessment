package org.flickit.flickitassessmentcore.application.port.in.assessment;

import java.util.List;

public interface GetAssessmentColorsUseCase {

    Result getAssessmentColors();

    record Result(ColorItem defaultColor, List<ColorItem> colors) {}

    record ColorItem(int id, String title, String code) {}
}

