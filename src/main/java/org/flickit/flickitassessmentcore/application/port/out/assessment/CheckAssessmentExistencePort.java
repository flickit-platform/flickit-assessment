package org.flickit.flickitassessmentcore.application.port.out.assessment;

import java.util.UUID;

public interface CheckAssessmentExistencePort {

    boolean existsById(UUID id, boolean deletion);
}
