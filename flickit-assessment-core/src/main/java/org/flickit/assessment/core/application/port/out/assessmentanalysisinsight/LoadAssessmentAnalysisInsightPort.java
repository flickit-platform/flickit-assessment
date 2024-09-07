package org.flickit.assessment.core.application.port.out.assessmentanalysisinsight;

import java.util.UUID;

public interface LoadAssessmentAnalysisInsightPort {

    String loadAssessmentAnalysisAiInsight(UUID assessmentId, int type);
}
