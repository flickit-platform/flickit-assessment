package org.flickit.flickitassessmentcore.application.port.out.assessment;

import java.util.UUID;

public interface SoftDeleteAssessmentPort {

    void setDeletionTimeById(UUID id, Long deletionTime);
}
