package org.flickit.flickitassessmentcore.application.port.out;

import java.util.UUID;

public interface SoftDeleteAssessmentPort {

    void setDeletionTimeById(Param param);

    record Param(UUID id, Long deletionTime) {
    }
}
