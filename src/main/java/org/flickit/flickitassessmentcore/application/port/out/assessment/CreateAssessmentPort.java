package org.flickit.flickitassessmentcore.application.port.out.assessment;

import org.flickit.flickitassessmentcore.application.port.in.assessment.CreateAssessmentCommand;

import java.util.UUID;

public interface CreateAssessmentPort {
    UUID persist(CreateAssessmentCommand createAssessmentCommand);
}
