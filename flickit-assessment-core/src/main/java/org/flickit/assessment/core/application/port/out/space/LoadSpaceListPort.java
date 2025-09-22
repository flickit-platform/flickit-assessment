package org.flickit.assessment.core.application.port.out.space;

import org.flickit.assessment.core.application.domain.Space;

import java.util.List;
import java.util.UUID;

public interface LoadSpaceListPort {

    List<SpaceWithAssessmentCount> loadByOwnerId(UUID ownerId);

    record SpaceWithAssessmentCount(Space space, int assessmentCount) {
    }
}
