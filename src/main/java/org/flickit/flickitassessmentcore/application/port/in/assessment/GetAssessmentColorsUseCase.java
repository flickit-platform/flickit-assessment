package org.flickit.flickitassessmentcore.application.port.in.assessment;

import org.flickit.flickitassessmentcore.application.domain.AssessmentColor;

import java.util.List;

public interface GetAssessmentColorsUseCase {

    List<AssessmentColor> getAssessmentColors();
}
