package org.flickit.assessment.kit.application.port.out.subject;

import java.time.LocalDateTime;
import java.util.List;

public interface UpdateSubjectPort {

    void update(List<Param> params);

    record Param(
        long id,
        String title,
        int index,
        String description,
        LocalDateTime lastModificationTime
    ) {
    }
}
