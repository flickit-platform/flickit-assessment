package org.flickit.assessment.core.application.port.out.evidence;

import java.time.LocalDateTime;
import java.util.UUID;

public interface UpdateEvidencePort {

    Result update(Param param);

    record Param(UUID id,
                 String description,
                 LocalDateTime lastModificationTime) {
    }

    record Result(UUID id) {
    }
}
