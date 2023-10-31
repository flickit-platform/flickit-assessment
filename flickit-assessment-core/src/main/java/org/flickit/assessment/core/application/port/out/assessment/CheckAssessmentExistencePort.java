package org.flickit.assessment.core.application.port.out.assessment;

import java.util.UUID;

public interface CheckAssessmentExistencePort {

    boolean existsById(UUID id);
}
