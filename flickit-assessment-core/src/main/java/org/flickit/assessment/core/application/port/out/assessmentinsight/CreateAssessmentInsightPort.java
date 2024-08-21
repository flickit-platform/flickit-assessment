package org.flickit.assessment.core.application.port.out.assessmentinsight;

import java.time.LocalDateTime;
import java.util.UUID;

public interface CreateAssessmentInsightPort {

    void createInsight(Param param);

    record Param(UUID assessmentResultId,
                 String insight,
                 LocalDateTime insightTime,
                 UUID insightBy) {
    }
}
