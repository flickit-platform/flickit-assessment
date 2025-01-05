package org.flickit.assessment.core.application.port.out.subjectinsight;

import java.util.UUID;

public interface ApproveSubjectInsightPort {

    void approve(UUID assessmentId, long subjectId);
}
