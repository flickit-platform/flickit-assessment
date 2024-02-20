package org.flickit.assessment.kit.application.port.out.kitdsl;

import java.time.LocalDateTime;
import java.util.UUID;

public interface UpdateKitDslPort {

    void update(Long id, Long kitId, UUID lastModifiedBy, LocalDateTime lastModificationTime);
}
