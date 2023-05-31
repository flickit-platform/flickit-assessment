package org.flickit.flickitassessmentcore.application.port.in.assessment;

import java.util.UUID;

public interface CreateAssessmentUseCase {
    UUID createAssessment(CreateAssessmentCommand command);
}
