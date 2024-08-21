package org.flickit.assessment.core.application.port.out.subjectinsight;

import java.time.LocalDateTime;
import java.util.UUID;

public interface CreateSubjectInsightPort {

    void persist(Param param);

    record Param(UUID assessmentResultId,
                 Long subjectId,
                 String insight,
                 LocalDateTime insightTime,
                 UUID insightBy) {
    }
}
