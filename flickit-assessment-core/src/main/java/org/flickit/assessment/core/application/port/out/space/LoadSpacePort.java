package org.flickit.assessment.core.application.port.out.space;

import org.flickit.assessment.core.application.domain.Space;

import java.util.Optional;
import java.util.UUID;

public interface LoadSpacePort {

    /**
     * Loads a Space by its unique identifier.
     *
     * @param spaceId the unique identifier of the space to load
     * @return an Optional containing the Space if found, empty Optional otherwise
     */
    Optional<Space> loadSpace(long spaceId);

    /**
     * Loads a Space by the associated assessment's UUID.
     *
     * @param assessmentId the UUID of the assessment associated with the space
     * @return an Optional containing the Space if found, empty Optional otherwise
     */
    Optional<Space> loadSpace(UUID assessmentId);
}
