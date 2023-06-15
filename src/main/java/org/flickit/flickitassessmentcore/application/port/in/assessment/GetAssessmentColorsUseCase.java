package org.flickit.flickitassessmentcore.application.port.in.assessment;

import org.flickit.flickitassessmentcore.domain.AssessmentColor;

import java.util.List;

public interface GetAssessmentColorsUseCase {

    List<AssessmentColor> getAssessmentColors();
}
