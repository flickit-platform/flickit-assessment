package org.flickit.assessment.kit.application.port.out.attribute;

import java.time.LocalDateTime;
import java.util.UUID;

public interface UpdateAttributePort {

    void update(Param param);

    record Param(
        long id,
        String title,
        int index,
        String description,
        int weight,
        LocalDateTime lastModificationTime,
        UUID lastModifiedBy,
        long subjectId
    ) {}
}
