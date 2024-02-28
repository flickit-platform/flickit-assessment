package org.flickit.assessment.advice.application.port.out.attributeleveltarget;

import org.flickit.assessment.advice.application.domain.AttributeLevelTarget;
import org.flickit.assessment.common.exception.ResourceNotFoundException;

import java.util.List;
import java.util.UUID;

public interface CreateAttributeLevelTargetPort {

    /**
     * @throws ResourceNotFoundException if no advice found by the given adviceId
     */
    void persistAll(UUID adviceId, List<AttributeLevelTarget> attributeLevelTargets);
}
