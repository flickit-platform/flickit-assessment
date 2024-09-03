package org.flickit.assessment.core.application.port.out.assessmentanalysis;

import org.flickit.assessment.core.application.domain.AssessmentAnalysis;

import java.util.Optional;
import java.util.UUID;

public interface LoadAssessmentAnalysisPort {

    Optional<AssessmentAnalysis> loadAssessmentAnalysis(UUID assessmentResultId, int type);
}
