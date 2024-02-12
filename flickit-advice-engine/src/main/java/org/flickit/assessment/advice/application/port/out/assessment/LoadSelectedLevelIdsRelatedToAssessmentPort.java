package org.flickit.assessment.advice.application.port.out.assessment;

import java.util.Set;
import java.util.UUID;

public interface LoadSelectedLevelIdsRelatedToAssessmentPort {

    Set<Long> load(UUID assessmentId, Set<Long> levelIds);
}
