package org.flickit.flickitassessmentcore.application.port.out.AssessmentColor;

import org.flickit.flickitassessmentcore.application.port.in.Assessment.AssessmentColorDto;

public interface LoadAssessmentColorByIdPort {
    AssessmentColorDto loadById(Long id);
}
