package org.flickit.assessment.core.application.port.out.assessment;

import org.flickit.assessment.core.application.domain.AnalysisType;

public interface CreateAssessmentAiAnalysisPort {

    String generateAssessmentAnalysis(String fileContent, AnalysisType analysisType);
}
