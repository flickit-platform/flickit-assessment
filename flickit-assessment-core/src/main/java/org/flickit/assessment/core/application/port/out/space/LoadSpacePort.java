package org.flickit.assessment.core.application.port.out.space;

import org.flickit.assessment.core.application.domain.Space;

import java.util.Optional;
import java.util.UUID;

public interface LoadSpacePort {

    Optional<Space> loadSpace(long spaceId);

    Optional<Space> loadAssessmentSpace(UUID assessmentId);
}
