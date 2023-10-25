package org.flickit.flickitassessmentcore.application.port.out.assessment;

import java.time.LocalDateTime;
import java.util.UUID;

public interface UpdateAssessmentByIdPort {

    void updateById(Param param);

    record Param(UUID id, LocalDateTime lastModificationTime) {
    }
}
