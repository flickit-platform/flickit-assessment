package org.flickit.assessment.core.application.port.out.assessmentanalysis;

import java.util.UUID;

public interface UpdateAssessmentAnalysisInputPathPort {

    void update(UUID id, String inputPath);
}
