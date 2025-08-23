package org.flickit.assessment.core.application.port.out.insight.attribute;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface UpdateAttributeInsightPort {

    void updateAiInsight(AiParam attributeInsight);

    void updateAiInsights(List<AiParam> attributeInsight);

    record AiParam(UUID assessmentResultId,
                   Long attributeId,
                   String aiInsight,
                   LocalDateTime aiInsightTime,
                   String aiInputPath,
                   boolean isApproved,
                   LocalDateTime lastModificationTime) {
    }

    void updateAiInsightTime(AiTimeParam attributeInsight);

    record AiTimeParam(UUID assessmentResultId,
                       Long attributeId,
                       LocalDateTime aiInsightTime,
                       LocalDateTime lastModificationTime) {
    }

    void updateAssessorInsight(AssessorParam attributeInsight);

    record AssessorParam(UUID assessmentResultId,
                         Long attributeId,
                         String assessorInsight,
                         LocalDateTime assessorInsightTime,
                         boolean isApproved,
                         LocalDateTime lastModificationTime) {
    }
}
