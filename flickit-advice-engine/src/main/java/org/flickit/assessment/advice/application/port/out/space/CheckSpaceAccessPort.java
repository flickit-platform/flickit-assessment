package org.flickit.assessment.advice.application.port.out.space;

import java.util.UUID;

public interface CheckSpaceAccessPort {

    boolean checkIsMember(long spaceId, UUID userId);
}
