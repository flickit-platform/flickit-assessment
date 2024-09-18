package org.flickit.assessment.core.application.port.out.assessmentanalysis;

import org.flickit.assessment.core.application.domain.AssessmentAnalysis;

import java.util.UUID;

public interface UpdateAssessmentAnalysisPort {

    void updateInputPath(UUID id, String inputPath);

    void updateAiAnalysis(AssessmentAnalysis assessmentAnalysis);
}
