package org.flickit.assessment.core.application.port.in.assessment;

import java.util.UUID;

public interface CreateAssessmentUseCase {

    UUID createAssessment(CreateAssessmentCommand command);
}
