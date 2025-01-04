package org.flickit.assessment.core.application.port.out.assessmentinsight;

import java.util.UUID;

public interface ApproveAssessmentInsightPort {

    void approve(UUID assessmentId);
}
