package org.flickit.assessment.kit.application.port.out.kitcustom;

import org.flickit.assessment.kit.application.domain.KitCustomData;

import java.time.LocalDateTime;
import java.util.UUID;

public interface CreateKitCustomPort {

    long persist(Param param);

    record Param(long kitId,
                 String title,
                 String code,
                 KitCustomData customData,
                 LocalDateTime creationTime,
                 UUID createdBy) {
    }
}
