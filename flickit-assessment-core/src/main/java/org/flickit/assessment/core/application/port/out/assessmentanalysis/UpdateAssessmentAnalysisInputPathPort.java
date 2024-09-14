package org.flickit.assessment.core.application.port.out.assessmentanalysis;

import java.util.UUID;

public interface UpdateAssessmentAnalysisInputPathPort {

    void updateInputPath(UUID id, String inputPath);
}
