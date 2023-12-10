package org.flickit.assessment.kit.application.port.out.attribute;

import java.time.LocalDateTime;

public interface UpdateAttributePort {

    void update(Param param);

    record Param(
        long id,
        String title,
        int index,
        String description,
        int weight,
        LocalDateTime lastModificationTime,
        long subjectId
    ) {}
}
