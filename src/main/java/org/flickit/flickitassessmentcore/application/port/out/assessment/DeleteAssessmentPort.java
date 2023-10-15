package org.flickit.flickitassessmentcore.application.port.out.assessment;

import java.util.UUID;

public interface DeleteAssessmentPort {

    void deleteById(UUID id, Long deletionTime);
}
