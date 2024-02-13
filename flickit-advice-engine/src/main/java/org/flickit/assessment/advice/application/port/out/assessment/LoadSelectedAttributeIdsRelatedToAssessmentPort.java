package org.flickit.assessment.advice.application.port.out.assessment;

import java.util.Set;
import java.util.UUID;

public interface LoadSelectedAttributeIdsRelatedToAssessmentPort {

    Set<Long> loadSelectedAttributeIdsRelatedToAssessment(UUID assessmentId, Set<Long> attributeIds);
}
