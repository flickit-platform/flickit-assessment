package org.flickit.assessment.kit.application.port.out.subject;

import java.time.LocalDateTime;

public interface UpdateSubjectsPort {

    void updateSubject(Param param);

    record Param(
        long kitId,
        String code,
        String title,
        String description,
        int index,
        LocalDateTime lastModificationTime
    ) {
    }
}
