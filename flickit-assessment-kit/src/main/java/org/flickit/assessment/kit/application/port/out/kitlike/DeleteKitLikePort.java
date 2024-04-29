package org.flickit.assessment.kit.application.port.out.kitlike;

import java.util.UUID;

public interface DeleteKitLikePort {

    void delete(Long kitId, UUID userId);
}
