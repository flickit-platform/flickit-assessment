package org.flickit.assessment.core.application.port.out.subjectinsight;

import java.util.UUID;

public interface CheckSubjectInsightExistPort {

    boolean exists(UUID assessmentResultId, Long subjectId);
}
