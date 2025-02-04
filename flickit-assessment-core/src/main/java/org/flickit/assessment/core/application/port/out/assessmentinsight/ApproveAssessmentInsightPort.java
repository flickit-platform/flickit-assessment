package org.flickit.assessment.core.application.port.out.assessmentinsight;

import java.time.LocalDateTime;
import java.util.UUID;

public interface ApproveAssessmentInsightPort {

    void approve(UUID assessmentId, LocalDateTime lastModificationTime);
}
