package org.flickit.assessment.kit.application.port.out.kitlike;

import java.util.UUID;

public interface CreateKitLikePort {

    void create(Long kitId, UUID userId);
}
