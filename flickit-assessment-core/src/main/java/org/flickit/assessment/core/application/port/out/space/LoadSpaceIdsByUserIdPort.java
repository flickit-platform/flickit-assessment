package org.flickit.assessment.core.application.port.out.space;

import java.util.List;
import java.util.UUID;

public interface LoadSpaceIdsByUserIdPort {

    List<Long> loadSpaceIdsByUserId(UUID userId);
}
