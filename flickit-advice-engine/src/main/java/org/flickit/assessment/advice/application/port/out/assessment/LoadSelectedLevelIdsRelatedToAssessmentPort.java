package org.flickit.assessment.advice.application.port.out.assessment;

import org.flickit.assessment.common.application.domain.ID;

import java.util.Set;
import java.util.UUID;

public interface LoadSelectedLevelIdsRelatedToAssessmentPort {

    Set<Long> loadSelectedLevelIdsRelatedToAssessment(ID assessmentId, Set<Long> levelIds);
}
