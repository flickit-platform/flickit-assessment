package org.flickit.assessment.kit.application.port.out.kitversion;

import java.time.LocalDateTime;
import java.util.UUID;

public interface UpdateKitVersionModificationInfoPort {

    void updateModificationInfo(long id, LocalDateTime modificationTime, UUID modifiedBy);
}
