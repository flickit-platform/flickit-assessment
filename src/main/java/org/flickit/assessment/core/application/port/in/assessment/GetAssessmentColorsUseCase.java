package org.flickit.assessment.core.application.port.in.assessment;

import org.flickit.assessment.core.domain.AssessmentColor;

import java.util.List;

public interface GetAssessmentColorsUseCase {

    List<AssessmentColor> getAssessmentColors();
}
