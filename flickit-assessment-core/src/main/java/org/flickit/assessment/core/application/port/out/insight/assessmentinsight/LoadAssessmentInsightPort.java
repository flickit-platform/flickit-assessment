package org.flickit.assessment.core.application.port.out.insight.assessmentinsight;

import org.flickit.assessment.core.application.domain.AssessmentInsight;

import java.util.Optional;
import java.util.UUID;

public interface LoadAssessmentInsightPort {

    Optional<AssessmentInsight> loadByAssessmentResultId(UUID assessmentResultId);
}
