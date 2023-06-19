package org.flickit.assessment.core.application.service.assessmentcolor;

import org.flickit.assessment.core.application.port.in.assessment.GetAssessmentColorsUseCase;
import org.flickit.assessment.core.domain.AssessmentColor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class GetAssessmentColorsService implements GetAssessmentColorsUseCase {

    @Override
    public List<AssessmentColor> getAssessmentColors() {
        return Arrays.asList(AssessmentColor.values());
    }
}
