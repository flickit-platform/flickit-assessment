package org.flickit.assessment.advice.application.port.out.assessment;

import java.util.UUID;

public interface AssessmentAttrLevelExistencePort {

    boolean exists(UUID assessmentId, Long attributeId, Long maturityLevelId);
}
