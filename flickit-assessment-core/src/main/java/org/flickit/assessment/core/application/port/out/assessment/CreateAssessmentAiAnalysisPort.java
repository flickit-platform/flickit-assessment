package org.flickit.assessment.core.application.port.out.assessment;

import org.flickit.assessment.core.application.domain.AssessmentAnalysisInsight;
import org.flickit.assessment.core.application.domain.AnalysisType;

public interface CreateAssessmentAiAnalysisPort {

    AssessmentAnalysisInsight generateAssessmentAnalysis(String title, String factSheet, AnalysisType analysisType);
}
