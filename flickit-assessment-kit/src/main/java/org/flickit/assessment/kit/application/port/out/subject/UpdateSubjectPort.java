package org.flickit.assessment.kit.application.port.out.subject;

import java.time.LocalDateTime;
import java.util.UUID;

public interface UpdateSubjectPort {

    void update(Param param);

    record Param(
        long id,
        long kitVersionId,
        String code,
        String title,
        int index,
        String description,
        int weight,
        LocalDateTime lastModificationTime,
        UUID lastModifiedBy
    ) {}
}
