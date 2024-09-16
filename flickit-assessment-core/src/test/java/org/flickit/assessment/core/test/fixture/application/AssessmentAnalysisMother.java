package org.flickit.assessment.core.test.fixture.application;

import org.flickit.assessment.core.application.domain.AnalysisType;
import org.flickit.assessment.core.application.domain.AssessmentAnalysis;

import java.time.LocalDateTime;
import java.util.UUID;

public class AssessmentAnalysisMother {

    public static AssessmentAnalysis assessmentAnalysis() {
        return new AssessmentAnalysis(
            UUID.randomUUID(),
            UUID.randomUUID(),
            AnalysisType.CODE_QUALITY,
            "Ai analysis",
            "Assessor analysis",
            LocalDateTime.now(),
            LocalDateTime.now(),
            "input/path"
        );
    }
}
