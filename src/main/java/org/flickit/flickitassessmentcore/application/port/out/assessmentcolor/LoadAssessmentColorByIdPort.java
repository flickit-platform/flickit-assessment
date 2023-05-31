package org.flickit.flickitassessmentcore.application.port.out.assessmentcolor;

import org.flickit.flickitassessmentcore.application.port.in.assessment.AssessmentColorDto;

public interface LoadAssessmentColorByIdPort {
    AssessmentColorDto loadById(Long id);
}
