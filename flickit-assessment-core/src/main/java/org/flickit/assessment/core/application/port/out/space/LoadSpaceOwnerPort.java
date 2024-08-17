package org.flickit.assessment.core.application.port.out.space;

import java.util.UUID;

public interface LoadSpaceOwnerPort {

    UUID loadOwnerId(long spaceId);

    UUID loadOwnerId(UUID assessmentId);
}
