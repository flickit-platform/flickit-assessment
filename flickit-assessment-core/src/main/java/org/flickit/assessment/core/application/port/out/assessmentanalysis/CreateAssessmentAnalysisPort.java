package org.flickit.assessment.core.application.port.out.assessmentanalysis;

import org.flickit.assessment.core.application.domain.AnalysisType;

import java.util.UUID;

public interface CreateAssessmentAnalysisPort {

    UUID persist(Param param);

    record Param(UUID assessmentResultId,
                 AnalysisType type,
                 String inputPath) {
    }
}
