package org.flickit.assessment.core.application.port.out.insight.assessment;

import org.flickit.assessment.core.application.domain.insight.AssessmentInsight;

import java.util.Optional;
import java.util.UUID;

public interface LoadAssessmentInsightPort {

    Optional<AssessmentInsight> loadByAssessmentResultId(UUID assessmentResultId);
}
