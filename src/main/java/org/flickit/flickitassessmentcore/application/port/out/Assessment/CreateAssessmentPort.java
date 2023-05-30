package org.flickit.flickitassessmentcore.application.port.out.Assessment;

import org.flickit.flickitassessmentcore.application.port.in.Assessment.CreateAssessmentCommand;

import java.util.UUID;

public interface CreateAssessmentPort {
    UUID persist(CreateAssessmentCommand createAssessmentCommand);
}
