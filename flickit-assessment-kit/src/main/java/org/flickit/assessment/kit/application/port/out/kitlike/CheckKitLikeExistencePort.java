package org.flickit.assessment.kit.application.port.out.kitlike;

import java.util.UUID;

public interface CheckKitLikeExistencePort {

    boolean exist(Long kitId, UUID userId);
}
