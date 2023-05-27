package org.flickit.flickitassessmentcore.application.port.in.AssessmentProject;

import java.util.UUID;

public interface CreateAssessmentProjectUseCase {
    UUID createAssessmentProject(CreateAssessmentProjectCommand createAssessmentProjectCommand);
}
