package org.flickit.flickitassessmentcore.application.port.out.assessmentresult;

import java.util.UUID;

public interface CheckAssessmentResultExistencePort {

    boolean existsById(UUID id);
}
