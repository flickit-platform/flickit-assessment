package org.flickit.flickitassessmentcore.application.port.out.AssessmentColor;

import org.flickit.flickitassessmentcore.application.port.in.AssessmentProject.AssessmentColorDto;

public interface LoadAssessmentColorByIdPort {
    AssessmentColorDto loadById(Long id);
}
