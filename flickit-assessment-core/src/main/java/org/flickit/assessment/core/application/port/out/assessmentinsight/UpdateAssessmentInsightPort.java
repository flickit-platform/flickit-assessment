package org.flickit.assessment.core.application.port.out.assessmentinsight;

import org.flickit.assessment.core.application.domain.AssessmentInsight;

import java.time.LocalDateTime;
import java.util.UUID;

public interface UpdateAssessmentInsightPort {

    void updateinsight(AssessmentInsight assessmentInsight);

    record Param(UUID id,
                 UUID insight,
                 LocalDateTime insightTime,
                 UUID insightBy) {
    }
}
