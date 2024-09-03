package org.flickit.assessment.core.test.fixture.application;

import org.flickit.assessment.core.application.domain.AssessmentAnalysis;

import java.util.UUID;

public class AssessmentAnalysisMother {

    public static AssessmentAnalysis createIntitalAssessmentAnalysis(UUID assessmentResultId, int type) {
        return new AssessmentAnalysis(UUID.randomUUID(),
            assessmentResultId,
            type,
            null,
            null,
            null,
            null,
            "path/to/input/file");
    }
}
