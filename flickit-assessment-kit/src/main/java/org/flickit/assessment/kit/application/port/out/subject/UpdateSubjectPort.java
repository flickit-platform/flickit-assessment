package org.flickit.assessment.kit.application.port.out.subject;

import java.time.LocalDateTime;

public interface UpdateSubjectPort {

    void updateByCodeAndKitId(Param param);

    record Param(
        String code,
        String title,
        int index,
        String description,
        LocalDateTime lastModificationTime,
        long kitId
    ) {
    }
}
