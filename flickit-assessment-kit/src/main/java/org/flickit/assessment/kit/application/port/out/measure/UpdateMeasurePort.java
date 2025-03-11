package org.flickit.assessment.kit.application.port.out.measure;

import java.time.LocalDateTime;
import java.util.UUID;

public interface UpdateMeasurePort {

    void update(Param param);

    record Param(
        long id,
        long kitVersionId,
        String title,
        String code,
        int index,
        String description,
        LocalDateTime lastModificationTime,
        UUID lastModifiedBy) {
    }
}
