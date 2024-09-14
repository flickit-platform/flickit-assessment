package org.flickit.assessment.core.application.port.out.assessmentanalysis;

import org.flickit.assessment.core.application.domain.AssessmentAnalysis;

public interface CreateAssessmentAnalysisPort {

    void persist(AssessmentAnalysis assessmentAnalysis);
}
