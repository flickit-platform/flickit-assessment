package org.flickit.assessment.core.application.port.out.subjectinsight;

import java.time.LocalDateTime;
import java.util.UUID;

public interface UpdateSubjectInsightPort {

    void update(Param param);

    record Param(UUID assessmentResultId, Long subjectId, String insight, UUID insightBy, LocalDateTime insightTime) {
    }
}
