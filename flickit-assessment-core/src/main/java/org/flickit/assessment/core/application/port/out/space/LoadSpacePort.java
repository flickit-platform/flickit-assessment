package org.flickit.assessment.core.application.port.out.space;

import org.flickit.assessment.core.application.domain.Space;

import java.util.Optional;

public interface LoadSpacePort {

    Optional<Space> loadSpace(long spaceId);
}
