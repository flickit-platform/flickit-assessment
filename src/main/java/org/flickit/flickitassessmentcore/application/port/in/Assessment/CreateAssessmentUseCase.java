package org.flickit.flickitassessmentcore.application.port.in.Assessment;

import java.util.UUID;

public interface CreateAssessmentUseCase {
    UUID createAssessment(CreateAssessmentCommand createAssessmentCommand);
}
