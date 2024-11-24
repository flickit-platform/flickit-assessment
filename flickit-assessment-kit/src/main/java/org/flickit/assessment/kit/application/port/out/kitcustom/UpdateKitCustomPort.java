package org.flickit.assessment.kit.application.port.out.kitcustom;

import org.flickit.assessment.kit.application.domain.KitCustomData;

import java.time.LocalDateTime;
import java.util.UUID;

public interface UpdateKitCustomPort {

    void update(Param param);

    record Param(long id,
                 long kitId,
                 String title,
                 String code,
                 KitCustomData customData,
                 LocalDateTime lastModificationTime,
                 UUID lastModifiedBy) {
    }
}
